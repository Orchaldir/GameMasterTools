package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseCalendar
import at.orchaldir.gm.core.action.CreateCalendar
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.time.DisplayYear
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.utils.doNothing
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

@Resource("/calendars")
class CalendarRoutes {
    @Resource("details")
    class Details(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("new")
    class New(val parent: CalendarRoutes = CalendarRoutes())

    @Resource("delete")
    class Delete(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("edit")
    class Edit(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("preview")
    class Preview(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("update")
    class Update(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())
}

fun Application.configureCalendarRouting() {
    routing {
        get<CalendarRoutes> {
            logger.info { "Get all calendars" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCalendars(call)
            }
        }
        get<CalendarRoutes.Details> { details ->
            logger.info { "Get details of calendar ${details.id.value}" }

            val state = STORE.getState()
            val calendar = state.getCalendarStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarDetails(call, state, calendar)
            }
        }
        get<CalendarRoutes.New> {
            logger.info { "Add new calendar" }

            STORE.dispatch(CreateCalendar)

            call.respondRedirect(
                call.application.href(
                    CalendarRoutes.Edit(
                        STORE.getState().getCalendarStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<CalendarRoutes.Delete> { delete ->
            logger.info { "Delete calendar ${delete.id.value}" }

            STORE.dispatch(DeleteCalendar(delete.id))

            call.respondRedirect(call.application.href(CalendarRoutes()))

            STORE.getState().save()
        }
        get<CalendarRoutes.Edit> { edit ->
            logger.info { "Get editor for calendar ${edit.id.value}" }

            val state = STORE.getState()
            val calendar = state.getCalendarStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarEditor(call, state, calendar)
            }
        }
        post<CalendarRoutes.Preview> { preview ->
            logger.info { "Preview changes to calendar ${preview.id.value}" }

            val state = STORE.getState()
            val calendar = parseCalendar(call.receiveParameters(), state.getDefaultCalendar(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarEditor(call, state, calendar)
            }
        }
        post<CalendarRoutes.Update> { update ->
            logger.info { "Update calendar ${update.id.value}" }

            val state = STORE.getState()
            val calendar = parseCalendar(call.receiveParameters(), state.getDefaultCalendar(), update.id)

            STORE.dispatch(UpdateCalendar(calendar))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCalendars(call: ApplicationCall) {
    val calendars = STORE.getState().getCalendarStorage().getAll().sortedBy { it.name }
    val count = calendars.size
    val createLink = call.application.href(CalendarRoutes.New())

    simpleHtml("Calendars") {
        field("Count", count.toString())
        showList(calendars) { calendar ->
            link(call, calendar)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCalendarDetails(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val backLink = call.application.href(CalendarRoutes())
    val deleteLink = call.application.href(CalendarRoutes.Delete(calendar.id))
    val editLink = call.application.href(CalendarRoutes.Edit(calendar.id))
    val cultures = state.getCultures(calendar.id)
    val holidays = state.getHolidays(calendar.id)

    simpleHtml("Calendar: ${calendar.name}") {
        field("Id", calendar.id.value.toString())
        field("Name", calendar.name)
        showOrigin(call, state, calendar)
        h2 { +"Parts" }
        showDays(calendar)
        showMonths(calendar)
        h2 { +"Eras" }
        showEras(call, state, calendar)
        h2 { +"Usage" }
        showList("Cultures", cultures) { culture ->
            link(call, culture)
        }
        showList("Holidays", holidays) { holiday ->
            link(call, holiday)
            +": "
            +holiday.relativeDate.display(calendar)
        }
        action(editLink, "Edit")
        if (state.canDelete(calendar.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun BODY.showOrigin(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val children = state.getChildren(calendar.id)

    when (calendar.origin) {
        is ImprovedCalendar -> {
            field("Origin", "Improved")
            field("Parent Calendar") {
                link(call, state, calendar.origin.parent)
            }
        }

        OriginalCalendar -> {
            field("Origin", "Original")
        }
    }
    showList("Child Calendars", children) { child ->
        link(call, child)
    }
}

private fun BODY.showDays(
    calendar: Calendar,
) {
    field("Days", calendar.days.getType().name)
    when (calendar.days) {
        is Weekdays -> showList("Weekdays", calendar.days.weekDays) { day ->
            +day.name
        }

        DayOfTheMonth -> doNothing()
    }
}

private fun BODY.showMonths(calendar: Calendar) {
    showList("Months", calendar.months) { month ->
        field(month.name, "${month.days} days")
    }
    field("Days per Year", calendar.getDaysPerYear().toString())
}

private fun BODY.showEras(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    field(call, state, "Start Date", calendar.getStartDate())
    field("Before Era", calendar.eras.display(DisplayYear(0, 0)))
    field("Current Era", calendar.eras.display(DisplayYear(1, 0)))
}

private fun HTML.showCalendarEditor(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val holidays = state.getHolidays(calendar.id)
    val backLink = href(call, calendar.id)
    val previewLink = call.application.href(CalendarRoutes.Preview(calendar.id))
    val updateLink = call.application.href(CalendarRoutes.Update(calendar.id))

    simpleHtml("Edit Calendar: ${calendar.name}") {
        field("Id", calendar.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(calendar.name)
            editOrigin(state, calendar)
            h2 { +"Parts" }
            editDays(calendar, holidays)
            editMonths(calendar, holidays)
            h2 { +"Eras" }
            editEras(calendar, state)
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.editDays(
    calendar: Calendar,
    holidays: List<Holiday>,
) {
    val days = calendar.days
    val supportsDayOfTheMonth = supportsDayOfTheMonth(holidays)

    field("Days") {
        select {
            id = DAYS
            name = DAYS
            onChange = ON_CHANGE_SCRIPT
            DaysType.entries.forEach {
                option {
                    label = it.name
                    value = it.name
                    disabled = it == DaysType.DayOfTheMonth && !supportsDayOfTheMonth
                    selected = it == days.getType()
                }
            }
        }
    }
    when (days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> {
            val minNumber = getMinNumberOfWeekdays(holidays)
            selectInt("Weekdays", days.weekDays.size, minNumber, 100, combine(WEEK, DAYS), true)
            days.weekDays.withIndex().forEach { (index, day) ->
                p {
                    selectText(day.name, combine(WEEK, DAY, index))
                }
            }
        }
    }
}

private fun FORM.editMonths(calendar: Calendar, holidays: List<Holiday>) {
    val minMonths = getMinNumberOfMonths(holidays)
    selectInt("Months", calendar.months.size, minMonths, 100, MONTHS, true)

    calendar.months.withIndex().forEach { (index, month) ->
        val minDays = getMinNumberOfDays(holidays, index)
        p {
            selectText(month.name, combine(MONTH, NAME, index))
            +": "
            selectInt(month.days, minDays, 100, combine(MONTH, DAYS, index))
            +"days"
        }
    }
}

private fun FORM.editOrigin(
    state: State,
    calendar: Calendar,
) {
    val origin = calendar.origin
    val possibleParents = state.getPossibleParents(calendar.id)

    field("Origin") {
        select {
            id = ORIGIN
            name = ORIGIN
            onChange = ON_CHANGE_SCRIPT
            CalendarOriginType.entries.forEach {
                option {
                    label = it.name
                    value = it.name
                    disabled = when (it) {
                        CalendarOriginType.Improved -> possibleParents.isEmpty()
                        CalendarOriginType.Original -> false
                    }
                    selected = when (it) {
                        CalendarOriginType.Improved -> origin is ImprovedCalendar
                        CalendarOriginType.Original -> origin is OriginalCalendar
                    }
                }
            }
        }
    }
    when (origin) {
        is ImprovedCalendar ->
            selectValue("Parent", CALENDAR, possibleParents) { c ->
                label = c.name
                value = c.id.value.toString()
                selected = origin.parent == c.id
            }

        else -> doNothing()
    }
}

private fun FORM.editEras(
    calendar: Calendar,
    state: State,
) {
    editEra("Before", calendar.eras.before, BEFORE)
    editEra("Current", calendar.eras.first, CURRENT)
    selectDate(state, "Start Date", calendar.getStartDate(), CURRENT)
}

private fun FORM.editEra(
    label: String,
    era: CalendarEra,
    param: String,
) {
    selectText("$label Era - Name", era.text, combine(param, NAME))
    selectBool("$label Era - Is prefix", era.isPrefix, combine(param, PREFIX))
}