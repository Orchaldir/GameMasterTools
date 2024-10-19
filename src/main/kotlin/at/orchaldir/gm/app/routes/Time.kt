package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseTime
import at.orchaldir.gm.core.action.UpdateTime
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.DisplayDay
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonPhase
import at.orchaldir.gm.core.selector.*
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

    @Resource("day")
    class ShowDay(val day: Day, val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

    @Resource("year")
    class ShowYear(val year: Year, val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

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
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the day ${data.day.day} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDay(call, calendarId, data.day)
            }
        }
        get<TimeRoutes.ShowYear> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the year ${data.year.year} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showYear(call, calendarId, data.year)
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
        field("Default Calendar") {
            link(call, state, state.time.defaultCalendar)
        }
        showCurrentDate(call, state)
        action(editLink, "Edit")
        back("/")
    }
}

private fun HTML.showDay(call: ApplicationCall, calendarId: CalendarId, day: Day) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayDay = calendar.resolve(day)
    val events = state.getEventsOfMonth(calendarId, day)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowDay(calendar.getStartOfNextMonth(day)))
    val previousLink = call.application.href(TimeRoutes.ShowDay(calendar.getStartOfPreviousMonth(day)))

    simpleHtml("Day: " + calendar.display(displayDay)) {
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
        showEvents(events, call, state, calendar)
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

private fun HTML.showYear(call: ApplicationCall, calendarId: CalendarId, year: Year) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayYear = calendar.resolve(year)
    val events = state.getEventsOfYear(calendarId, year)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowYear(year.nextYear()))
    val previousLink = call.application.href(TimeRoutes.ShowYear(year.previousYear()))

    simpleHtml("Year: " + calendar.display(displayYear)) {
        field("Calendar") {
            link(call, calendar)
        }
        action(nextLink, "Next Year")
        action(previousLink, "Previous Year")
        showEvents(events, call, state, calendar)
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
            selectValue("Default Calendar", CALENDAR, state.getCalendarStorage().getAll()) { calendar ->
                label = calendar.name
                value = calendar.id.value.toString()
                selected = calendar.id == state.time.defaultCalendar
            }
            selectDate(state, "Current Date", state.time.currentDate, CURRENT)
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun HTML.showEvents(call: ApplicationCall, calendarId: CalendarId) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents()
    val backLink = call.application.href(TimeRoutes())

    simpleHtml("Events") {
        field("Calendar") {
            link(call, calendar)
        }
        showCurrentDate(call, state)
        showEvents(events, call, state, calendar)
        back(backLink)
    }
}

private fun HtmlBlockTag.showEvents(
    unsortedEvents: List<Event>,
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val events = unsortedEvents.sort(calendar)

    showList("Events", events) { event ->
        val date = event.getDate()

        if (date is Day && date == state.time.currentDate) {
            link(call, date, "Today")
        } else {
            link(call, calendar, date)
        }
        +": "
        when (event) {
            is ArchitecturalStyleStartEvent -> {
                link(call, state, event.style)
                +" style started."
            }

            is ArchitecturalStyleEndEvent -> {
                link(call, state, event.style)
                +" style ended."
            }

            is BuildingConstructedEvent -> {
                link(call, state, event.buildingId)
                +" was constructed."
            }

            is BuildingOwnershipChangedEvent -> {
                link(call, state, event.buildingId)
                +"'s owner changed from "
                showOwner(call, state, event.from)
                +" to "
                showOwner(call, state, event.to)
                +"."
            }

            is CharacterDeathEvent -> {
                link(call, state, event.characterId)
                +" died."
            }

            is CharacterOriginEvent -> {
                link(call, state, event.characterId)
                +" was born."
            }

            is TownFoundingEvent -> {
                link(call, state, event.townId)
                +" was founded."
            }
        }
    }
}