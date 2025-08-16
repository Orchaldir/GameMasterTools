package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/time")
class TimeRoutes {

    @Resource("day")
    class ShowDay(val day: Day, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    // Using DayRange directly results in the parameter for start & end being named "day".
    @Resource("range")
    class ShowDayRange(val start: Int, val end: Int, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

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

    @Resource("millennium")
    class ShowMillennium(val millennium: Millennium, val calendar: CalendarId, val parent: TimeRoutes = TimeRoutes())

    @Resource("events")
    class ShowEvents(val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())
}

fun Application.configureTimeRouting() {
    routing {
        get<TimeRoutes.ShowDay> { data ->
            logger.info { "Show the day ${data.day.day} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDay(call, STORE.getState(), data.calendar, data.day)
            }
        }
        get<TimeRoutes.ShowDayRange> { data ->
            logger.info { "Show the range ${data.start}-${data.end} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, STORE.getState(), data.calendar, DayRange(data.start, data.end), "Range")
            }
        }
        get<TimeRoutes.ShowWeek> { data ->
            logger.info { "Show the week ${data.week.week} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showWeek(call, STORE.getState(), data.calendar, data.week)
            }
        }
        get<TimeRoutes.ShowMonth> { data ->
            logger.info { "Show the month ${data.month.month} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showMonth(call, STORE.getState(), data.calendar, data.month)
            }
        }
        get<TimeRoutes.ShowYear> { data ->
            logger.info { "Show the year ${data.year.year} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, STORE.getState(), data.calendar, data.year, "Year")
            }
        }
        get<TimeRoutes.ShowDecade> { data ->
            logger.info { "Show the decade ${data.decade} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, STORE.getState(), data.calendar, data.decade, "Decade")
            }
        }
        get<TimeRoutes.ShowCentury> { data ->
            logger.info { "Show the century ${data.century} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, STORE.getState(), data.calendar, data.century, "Century")
            }
        }
        get<TimeRoutes.ShowMillennium> { data ->
            logger.info { "Show the millennium ${data.millennium} for calendar ${data.calendar.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, STORE.getState(), data.calendar, data.millennium, "Millennium")
            }
        }
        get<TimeRoutes.ShowEvents> { data ->
            val calendarId = data.calendar ?: STORE.getState().getDefaultCalendarId()
            logger.info { "Show events with calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showEvents(call, calendarId)
            }
        }
    }
}


