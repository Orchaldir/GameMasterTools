package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.showCurrentDate
import at.orchaldir.gm.app.html.model.showOwner
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.selector.getEvents
import at.orchaldir.gm.core.selector.sort
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.LI


fun HTML.showEvents(call: ApplicationCall, calendarId: CalendarId) {
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

fun HtmlBlockTag.showEvents(
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