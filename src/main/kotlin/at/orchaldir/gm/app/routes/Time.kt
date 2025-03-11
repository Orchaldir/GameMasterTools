package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.field
import at.orchaldir.gm.app.html.model.selectDate
import at.orchaldir.gm.app.html.model.showCurrentDate
import at.orchaldir.gm.app.html.model.showOwner
import at.orchaldir.gm.app.parse.parseTime
import at.orchaldir.gm.core.action.UpdateTime
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonPhase
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.Coterminous
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.time.display
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.resolve
import at.orchaldir.gm.core.selector.world.getPlanarAlignments
import at.orchaldir.gm.utils.Id
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

    @Resource("decade")
    class ShowDecade(val decade: Decade, val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

    @Resource("century")
    class ShowCentury(val century: Century, val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

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
        get<TimeRoutes.ShowDecade> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the decade ${data.decade} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDecade(call, calendarId, data.decade)
            }
        }
        get<TimeRoutes.ShowCentury> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the century ${data.century} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showCentury(call, calendarId, data.century)
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

private fun HTML.showDay(call: ApplicationCall, calendarId: CalendarId, day: Day) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayDay = calendar.resolve(day)
    val events = state.getEventsOfMonth(calendarId, day)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowDay(calendar.getStartOfNextMonth(day)))
    val previousLink = call.application.href(TimeRoutes.ShowDay(calendar.getStartOfPreviousMonth(day)))
    val yearLink = call.application.href(TimeRoutes.ShowYear(calendar.getYear(day)))

    simpleHtml("Day: " + display(calendar, displayDay)) {
        fieldLink("Calendar", call, state, calendar)
        showMap("Planar Alignments", state.getPlanarAlignments(day)) { plane, alignment ->
            link(call, plane)
            +" ($alignment)"
        }
        action(nextLink, "Next Month")
        action(previousLink, "Previous Month")
        action(yearLink, "Show Year")

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

                            showMoons(call, moons, day)

                            showList(state.getForHolidays(day)) { holiday ->
                                link(call, holiday)
                            }

                            val planes = state.getPlanarAlignments(day)
                                .filterValues { alignment -> alignment == Coterminous }

                            showMap(planes) { plane, alignment ->
                                link(call, plane)
                                +" ($alignment)"
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
    call: ApplicationCall,
    moons: Collection<Moon>,
    day: Day,
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
    val decade = calendar.resolve(displayYear.decade())
    val events = state.getEventsOfYear(calendarId, year)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowYear(year.nextYear()))
    val previousLink = call.application.href(TimeRoutes.ShowYear(year.previousYear()))
    val decadeLink = call.application.href(TimeRoutes.ShowDecade(decade))

    simpleHtml("Year: " + display(calendar, displayYear)) {
        fieldLink("Calendar", call, state, calendar)
        showMap("Planar Alignments", state.getPlanarAlignments(year)) { plane, alignment ->
            link(call, plane)
            +" ($alignment)"
        }
        action(nextLink, "Next Year")
        action(previousLink, "Previous Year")
        action(decadeLink, "Show Decade")

        showEvents(events, call, state, calendar)
        back(backLink)
    }
}

private fun HTML.showDecade(call: ApplicationCall, calendarId: CalendarId, decade: Decade) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayDecade = calendar.resolve(decade)
    val events = state.getEventsOfDecade(calendarId, decade)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowDecade(decade.nextDecade()))
    val previousLink = call.application.href(TimeRoutes.ShowDecade(decade.previousDecade()))

    simpleHtml("Decade: " + display(calendar, displayDecade)) {
        fieldLink("Calendar", call, state, calendar)
        field(call, "Start", calendar, calendar.getStartOfDecade(decade))
        field(call, "End", calendar, calendar.getEndOfDecade(decade))
        action(nextLink, "Next Decade")
        action(previousLink, "Previous Decade")
        showEvents(events, call, state, calendar)
        back(backLink)
    }
}

private fun HTML.showCentury(call: ApplicationCall, calendarId: CalendarId, century: Century) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEventsOfCentury(calendarId, century)
    val backLink = call.application.href(TimeRoutes())
    val nextLink = call.application.href(TimeRoutes.ShowCentury(century.nextDecade()))
    val previousLink = call.application.href(TimeRoutes.ShowCentury(century.previousDecade()))

    simpleHtml("Century: " + display(calendar, century)) {
        fieldLink("Calendar", call, state, calendar)
        field(call, "Start", calendar, calendar.getStartOfCentury(century))
        field(call, "End", calendar, calendar.getEndOfCentury(century))
        action(nextLink, "Next Century")
        action(previousLink, "Previous Century")
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

private fun HTML.showEvents(call: ApplicationCall, calendarId: CalendarId) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents()
    val backLink = call.application.href(TimeRoutes())

    simpleHtml("Events") {
        fieldLink("Calendar", call, state, calendar)
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
        val date = event.date

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
                link(call, state, event.building)
                +" was constructed."
            }

            is BusinessStartedEvent -> {
                link(call, state, event.business)
                +" was started."
            }

            is BuildingOwnershipChangedEvent -> handleOwnershipChanged(call, state, event)

            is BusinessOwnershipChangedEvent -> handleOwnershipChanged(call, state, event)

            is OwnershipChangedEvent<*> -> doNothing()

            is CharacterDeathEvent -> {
                link(call, state, event.character)
                +" died."
            }

            is CharacterOriginEvent -> {
                link(call, state, event.character)
                +" was born."
            }

            is FontCreatedEvent -> {
                link(call, state, event.font)
                +" was created."
            }

            is OrganizationFoundingEvent -> {
                link(call, state, event.organization)
                +" was founded."
            }

            is RaceCreatedEvent -> {
                +"The race "
                link(call, state, event.race)
                +" was created."
            }

            is SpellCreatedEvent -> {
                link(call, state, event.spell)
                +" was created."
            }

            is TextPublishedEvent -> {
                link(call, state, event.text)
                +" was published."
            }

            is TownFoundingEvent -> {
                link(call, state, event.town)
                +" was founded."
            }
        }
    }
}

private fun <ID : Id<ID>> LI.handleOwnershipChanged(
    call: ApplicationCall,
    state: State,
    event: OwnershipChangedEvent<ID>,
) {
    link(call, state, event.id)
    +"'s owner changed from "
    showOwner(call, state, event.from)
    +" to "
    showOwner(call, state, event.to)
    +"."
}