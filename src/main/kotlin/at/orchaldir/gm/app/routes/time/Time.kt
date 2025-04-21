package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.selectDate
import at.orchaldir.gm.app.html.model.showCurrentDate
import at.orchaldir.gm.app.parse.parseTime
import at.orchaldir.gm.core.action.UpdateTime
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/time")
class TimeRoutes {

    @Resource("day")
    class ShowDay(val day: Day, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("range")
    class ShowDayRange(val range: DayRange, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("week")
    class ShowWeek(val week: Week, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("month")
    class ShowMonth(val month: Month, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("year")
    class ShowYear(val year: Year, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("decade")
    class ShowDecade(val decade: Decade, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("century")
    class ShowCentury(val century: Century, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("events")
    class ShowEvents(val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

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
        get<TimeRoutes.ShowDay> { data ->
            logger.info { "Show the day ${data.day.day} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDay(call, data.calendar, data.day)
            }
        }
        get<TimeRoutes.ShowDayRange> { data ->
            logger.info { "Show the range ${data.range.startDay.day}-${data.range.endDay.day} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, data.calendar, data.range, "Range")
            }
        }
        get<TimeRoutes.ShowWeek> { data ->
            logger.info { "Show the week ${data.week.week} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showWeek(call, data.calendar, data.week)
            }
        }
        get<TimeRoutes.ShowMonth> { data ->
            logger.info { "Show the month ${data.month.month} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showMonth(call, data.calendar, data.month)
            }
        }
        get<TimeRoutes.ShowYear> { data ->
            logger.info { "Show the year ${data.year.year} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, data.calendar, data.year, "Year")
            }
        }
        get<TimeRoutes.ShowDecade> { data ->
            logger.info { "Show the decade ${data.decade} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, data.calendar, data.decade, "Decade")
            }
        }
        get<TimeRoutes.ShowCentury> { data ->
            logger.info { "Show the century ${data.century} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, data.calendar, data.century, "Century")
            }
        }
        get<TimeRoutes.ShowEvents> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show events with calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showEvents(call, calendarId)
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
        fieldLink("Default Calendar", call, state, state.time.defaultCalendar)
        showCurrentDate(call, state)
        action(editLink, "Edit")
        back("/")
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
            selectElement(
                state,
                "Default Calendar",
                CALENDAR,
                state.getCalendarStorage().getAll(),
                state.time.defaultCalendar,
            )
            selectDate(state, "Current Date", state.time.currentDate, CURRENT)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
