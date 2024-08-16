package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.CURRENT
import at.orchaldir.gm.app.parse.parseTime
import at.orchaldir.gm.core.action.UpdateTime
import at.orchaldir.gm.core.model.calendar.CALENDAR
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.calendar.Weekdays
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.selector.getDefaultCalendar
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

    @Resource("month")
    class ShowMonth(val calendar: CalendarId, val day: Day, val parent: TimeRoutes = TimeRoutes())

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
        get<TimeRoutes.ShowMonth> { data ->
            logger.info { "Show month of day ${data.day.day} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showMonth(call, data.calendar, data.day)
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
    val showMonthLink = call.application.href(TimeRoutes.ShowMonth(state.time.defaultCalendar, state.time.currentDate))

    simpleHtml("Time Data") {
        field("Default Calendar") {
            link(call, state, state.time.defaultCalendar)
        }
        field(state, "Current Date", state.time.currentDate)
        action(editLink, "Edit")
        action(showMonthLink, "Show Month")
        back("/")
    }
}

private fun HTML.showMonth(call: ApplicationCall, calendarId: CalendarId, day: Day) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val month = calendar.getMonth(day)
    val startOfMonth = calendar.getStartOfMonthDate(day)
    val backLink = call.application.href(TimeRoutes())

    simpleHtml("Show Month") {
        field("Calendar") {
            link(call, calendar)
        }
        field("Date", calendar, day)
        when (calendar.days) {
            DayOfTheMonth -> doNothing()
            is Weekdays -> {
                table {
                    tr {
                        th {
                            colSpan = calendar.days.weekDays.size.toString()
                            +month.name
                        }
                    }
                    tr {
                        calendar.days.weekDays.forEach {
                            th {
                                +it.name
                            }
                        }
                    }
                    val startIndex = calendar.getWeekDay(startOfMonth)
                    var dayIndex = -startIndex
                    val minDaysShown = startIndex + month.days
                    val weeksShown = minDaysShown.ceilDiv(calendar.days.weekDays.size)

                    repeat(weeksShown) {
                        tr {
                            repeat(calendar.days.weekDays.size) {
                                td {
                                    if (month.isInside(dayIndex)) {
                                        +(dayIndex + 1).toString()
                                    }
                                    dayIndex++
                                }
                            }
                        }
                    }
                }
            }
        }
        back(backLink)
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