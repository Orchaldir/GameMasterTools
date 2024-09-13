package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.parse.parseTime
import at.orchaldir.gm.core.action.UpdateTime
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.moon.Moon
import at.orchaldir.gm.core.model.moon.MoonPhase
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.DisplayDay
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.getForHolidays
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ceilDiv
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

@Resource("/time")
class TimeRoutes {

    @Resource("show")
    class ShowDate(val day: Day, val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

    @Resource("edit")
    class Edit(val parent: TimeRoutes = TimeRoutes())

    @Resource("update")
    class Update(val parent: TimeRoutes = TimeRoutes())
}

fun Application.configureTimeRouting() {
    routing {
        get<TimeRoutes> {
            logger.info { "Get time data" }

            call.respondHtml(HttpStatusCode.OK) {
                showTimeData(call)
            }
        }
        get<TimeRoutes.ShowDate> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show month of day ${data.day.day} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showMonth(call, calendarId, data.day)
            }
        }
        get<TimeRoutes.Edit> {
            logger.info { "Get editor for time data" }

            call.respondHtml(HttpStatusCode.OK) {
                editTimeData(call)
            }
        }
        post<TimeRoutes.Update> {
            logger.info { "Update time data" }

            val time = parseTime(call.receiveParameters(), STORE.getState().getDefaultCalendar())

            STORE.dispatch(UpdateTime(time))

            call.respondRedirect(call.application.href(TimeRoutes()))

            STORE.getState().save()
        }
    }
}

private fun HTML.showTimeData(call: ApplicationCall) {
    val state = STORE.getState()
    val editLink = call.application.href(TimeRoutes.Edit())

    simpleHtml("Time Data") {
        field("Default Calendar") {
            link(call, state, state.time.defaultCalendar)
        }
        field(call, state, "Current Date", state.time.currentDate)
        action(editLink, "Edit")
        back("/")
    }
}

private fun HTML.showMonth(call: ApplicationCall, calendarId: CalendarId, day: Day) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayDay = calendar.resolve(day)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowDate(calendar.getStartOfNextMonth(day)))
    val previousLink = call.application.href(TimeRoutes.ShowDate(calendar.getStartOfPreviousMonth(day)))

    simpleHtml("Date: " + calendar.display(displayDay)) {
        field("Calendar") {
            link(call, calendar)
        }
        action(nextLink, "Next Month")
        action(previousLink, "Previous Month")
        when (calendar.days) {
            DayOfTheMonth -> doNothing()
            is Weekdays -> showMonthWithWeekDays(
                call,
                state,
                calendar,
                displayDay,
                calendar.days,
            )
        }
        back(backLink)
    }
}

private fun BODY.showMonthWithWeekDays(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
    selectedDay: DisplayDay,
    days: Weekdays,
) {
    val moons = state.getMoonStorage().getAll()
    val month = calendar.getMonth(selectedDay)
    val startOfMonth = calendar.getStartOfMonth(selectedDay)

    table {
        tr {
            th {
                colSpan = days.weekDays.size.toString()
                +month.name
            }
        }
        tr {
            days.weekDays.forEach {
                th {
                    +it.name
                }
            }
        }
        val startIndex = calendar.getWeekDay(startOfMonth) ?: 0
        var dayIndex = -startIndex
        val minDaysShown = startIndex + month.days
        val weeksShown = minDaysShown.ceilDiv(days.weekDays.size)

        repeat(weeksShown) {
            tr {
                repeat(days.weekDays.size) {
                    td {
                        if (month.isInside(dayIndex)) {
                            val day = startOfMonth + dayIndex

                            if (selectedDay.dayIndex == dayIndex) {
                                style = "background-color:cyan"
                            }

                            +(dayIndex + 1).toString()

                            showMoons(moons, day, call)

                            showList(state.getForHolidays(day)) { holiday ->
                                link(call, holiday)
                            }
                        }
                        dayIndex++
                    }
                }
            }
        }
    }
}

private fun TD.showMoons(
    moons: Collection<Moon>,
    day: Day,
    call: ApplicationCall,
) {
    moons.forEach {
        when (it.getPhase(day)) {
            MoonPhase.NewMoon -> showIcon(call, it, "New Moon", "new-moon.svg")
            MoonPhase.FullMoon -> showIcon(call, it, "Full Moon", "full-moon.svg")
            else -> doNothing()
        }
    }
}

private fun TD.showIcon(
    call: ApplicationCall,
    moon: Moon,
    text: String,
    filename: String,
) {
    link(call, moon.id) {
        abbr {
            title = text
            img {
                src = "/static/$filename"
                width = "16p"
            }
        }
    }
}

private fun HTML.editTimeData(
    call: ApplicationCall,
) {
    val state = STORE.getState()
    val backLink = call.application.href(TimeRoutes())
    val updateLink = call.application.href(TimeRoutes.Update())

    simpleHtml("Edit Time Data") {
        form {
            selectEnum("Default Calendar", CALENDAR, state.getCalendarStorage().getAll()) { calendar ->
                label = calendar.name
                value = calendar.id.value.toString()
                selected = calendar.id == state.time.defaultCalendar
            }
            selectDate(state, "Current Date", state.time.currentDate, CURRENT)
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