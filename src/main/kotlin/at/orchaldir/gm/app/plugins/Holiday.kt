package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateHoliday
import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.holiday.*
import at.orchaldir.gm.core.selector.canDelete
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/holiday")
class Holidays {
    @Resource("details")
    class Details(val id: HolidayId, val parent: Holidays = Holidays())

    @Resource("new")
    class New(val parent: Holidays = Holidays())

    @Resource("delete")
    class Delete(val id: HolidayId, val parent: Holidays = Holidays())

    @Resource("edit")
    class Edit(val id: HolidayId, val parent: Holidays = Holidays())

    @Resource("preview")
    class Preview(val id: HolidayId, val parent: Holidays = Holidays())

    @Resource("update")
    class Update(val id: HolidayId, val parent: Holidays = Holidays())
}

fun Application.configureHolidayRouting() {
    routing {
        get<Holidays> {
            logger.info { "Get all holidays" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllHolidays(call)
            }
        }
        get<Holidays.Details> { details ->
            logger.info { "Get details of holiday ${details.id.value}" }

            val state = STORE.getState()
            val holiday = state.getHolidayStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayDetails(call, state, holiday)
            }
        }
        get<Holidays.New> {
            logger.info { "Add new holiday" }

            STORE.dispatch(CreateHoliday)

            call.respondRedirect(call.application.href(Holidays.Edit(STORE.getState().getHolidayStorage().lastId)))

            STORE.getState().save()
        }
        get<Holidays.Delete> { delete ->
            logger.info { "Delete holiday ${delete.id.value}" }

            STORE.dispatch(DeleteHoliday(delete.id))

            call.respondRedirect(call.application.href(Holidays()))

            STORE.getState().save()
        }
        get<Holidays.Edit> { edit ->
            logger.info { "Get editor for holiday ${edit.id.value}" }

            val state = STORE.getState()
            val holiday = state.getHolidayStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayEditor(call, state, holiday)
            }
        }
        post<Holidays.Preview> { preview ->
            logger.info { "Get preview for holiday ${preview.id.value}" }

            val holiday = parseHoliday(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayEditor(call, STORE.getState(), holiday)
            }
        }
        post<Holidays.Update> { update ->
            logger.info { "Update holiday ${update.id.value}" }

            val holiday = parseHoliday(update.id, call.receiveParameters())

            STORE.dispatch(UpdateHoliday(holiday))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllHolidays(call: ApplicationCall) {
    val holiday = STORE.getState().getHolidayStorage().getAll().sortedBy { it.name }
    val count = holiday.size
    val createLink = call.application.href(Holidays.New())

    simpleHtml("Holidays") {
        field("Count", count.toString())
        showList(holiday) { holiday ->
            link(call, holiday)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showHolidayDetails(
    call: ApplicationCall,
    state: State,
    holiday: Holiday,
) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)
    val backLink = call.application.href(Holidays())
    val deleteLink = call.application.href(Holidays.Delete(holiday.id))
    val editLink = call.application.href(Holidays.Edit(holiday.id))

    simpleHtml("Holiday: ${holiday.name}") {
        field("Id", holiday.id.value.toString())
        field("Name", holiday.name)
        field("Calendar") {
            link(call, state, holiday.calendar)
        }
        field("Relative Date", holiday.relativeDate.display(calendar))
        action(editLink, "Edit")
        if (state.canDelete(holiday.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showHolidayEditor(
    call: ApplicationCall,
    state: State,
    holiday: Holiday,
) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)
    val backLink = href(call, holiday.id)
    val previewLink = call.application.href(Holidays.Preview(holiday.id))
    val updateLink = call.application.href(Holidays.Update(holiday.id))

    simpleHtml("Edit Holiday: ${holiday.name}") {
        field("Id", holiday.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                textInput(name = NAME) {
                    value = holiday.name
                }
            }
            selectEnum("Calendar", CALENDAR, state.getCalendarStorage().getAll(), true) { calendar ->
                label = calendar.name
                value = calendar.id.value.toString()
                selected = calendar.id == holiday.calendar
            }
            selectRelativeDate(DATE, holiday.relativeDate, calendar)
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        back(backLink)
    }
}

private fun FORM.selectRelativeDate(param: String, relativeDate: RelativeDate, calendar: Calendar) {
    selectEnum("Relative Date", combine(param, TYPE), RelativeDateType.entries, true) { type ->
        label = type.name
        value = type.name
        disabled = when (type) {
            RelativeDateType.FixedDayInYear -> false
            RelativeDateType.WeekdayInMonth -> calendar.days.getType() == DaysType.DayOfTheMonth
        }
        selected = type == relativeDate.getType()
    }
    when (relativeDate) {
        is FixedDayInYear -> {
            selectMonthIndex("Month", param, calendar, relativeDate.monthIndex)
            selectDayIndex(
                "Day",
                param,
                calendar,
                relativeDate.monthIndex,
                relativeDate.dayIndex,
            )
        }

        is WeekdayInMonth -> {
            selectMonthIndex("Month", param, calendar, relativeDate.monthIndex)
            when (calendar.days) {
                DayOfTheMonth -> error("WeekdayInMonth doesn't support DayOfTheMonth!")
                is Weekdays -> selectWithIndex(
                    "Weekday",
                    combine(param, DAY),
                    calendar.days.weekDays
                ) { index, weekday ->
                    label = weekday.name
                    value = index.toString()
                    selected = relativeDate.weekdayIndex == index
                }
            }
            selectNumber("Week", relativeDate.weekInMonthIndex, 0, 2, combine(param, WEEK))
        }
    }
}
