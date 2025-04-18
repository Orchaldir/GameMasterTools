package at.orchaldir.gm.app.routes.time

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
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonPhase
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.Coterminous
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.*
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

    @Resource("month")
    class ShowMonth(val month: Month, val calendar: CalendarId? = null, val parent: TimeRoutes = TimeRoutes())

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
        get<TimeRoutes.ShowMonth> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the month ${data.month.month} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showMonth(call, calendarId, data.month)
            }
        }
        get<TimeRoutes.ShowYear> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the year ${data.year.year} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, calendarId, data.year, "Year")
            }
        }
        get<TimeRoutes.ShowDecade> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the decade ${data.decade} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, calendarId, data.decade, "Decade")
            }
        }
        get<TimeRoutes.ShowCentury> { data ->
            val calendarId = data.calendar ?: STORE.getState().time.defaultCalendar
            logger.info { "Show the century ${data.century} for calendar ${calendarId.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showDate(call, calendarId, data.century, "Century")
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
    val displayDay = calendar.resolveDay(day)
    val monthLink = call.application.href(TimeRoutes.ShowMonth(calendar.resolveMonth(displayDay.month)))

    showDate(call, calendarId, day, "Day") {
        action(monthLink, "Show Month")

        visualizeMonth(call, state, calendar, displayDay.month, displayDay)
    }
}

private fun HtmlBlockTag.visualizeMonth(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
    displayMonth: DisplayMonth,
    displayDay: DisplayDay? = null,
) {
    when (calendar.days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> visualizeMonthWithWeekDays(
            call,
            state,
            calendar,
            displayMonth,
            displayDay,
            calendar.days,
        )
    }
}

private fun HtmlBlockTag.visualizeMonthWithWeekDays(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
    displayMonth: DisplayMonth,
    selectedDay: DisplayDay?,
    days: Weekdays,
) {
    val moons = state.getMoonStorage().getAll()
    val month = calendar.getMonth(displayMonth)
    val startOfMonth = calendar.resolveDay(calendar.getStartDisplayDayOfMonth(displayMonth))

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

                            if (selectedDay?.dayIndex == dayIndex) {
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

private fun HTML.showMonth(call: ApplicationCall, calendarId: CalendarId, month: Month) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayMonth = calendar.resolveMonth(month)
    val year = calendar.resolveYear(displayMonth.year)
    val yearLink = call.application.href(TimeRoutes.ShowYear(year))

    showDate(call, calendarId, month, "Month") {
        action(yearLink, "Show Year")

        visualizeMonth(call, state, calendar, displayMonth)
    }
}

private fun HTML.showDate(
    call: ApplicationCall,
    calendarId: CalendarId,
    date: Date,
    label: String,
    content: HtmlBlockTag.() -> Unit = {},
) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents(calendarId, date)
    val backLink = call.application.href(TimeRoutes())

    simpleHtml("$label: " + display(calendar, date)) {
        fieldLink("Calendar", call, state, calendar)
        field(call, "Start", calendar, calendar.getStartDay(date))
        field(call, "End", calendar, calendar.getEndDay(date))

        action { link(call, date.next(), "Next $label") }
        action { link(call, date.previous(), "Previous $label") }

        content()

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