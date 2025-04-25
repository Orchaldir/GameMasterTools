package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.showCurrentDate
import at.orchaldir.gm.app.html.model.showOwner
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getEvents
import at.orchaldir.gm.core.selector.sort
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

fun HTML.showEvents(call: ApplicationCall, calendarId: CalendarId) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents(calendar)
    val backLink = call.application.href(TimeRoutes())

    simpleHtml("Events") {
        fieldLink("Calendar", call, state, calendar)
        showCurrentDate(call, state)
        showEvents(events, call, state, calendar)
        back(backLink)
    }
}

fun HtmlBlockTag.showEvents(
    unsortedEvents: List<Event<*>>,
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val events = unsortedEvents.sort(calendar)

    table {
        tr {
            th { +"Date" }
            th { +"Event" }
        }
        events.forEach { event ->
            tr {
                td {
                    val date = event.date

                    if (date is Day && date == state.time.currentDate) {
                        link(call, calendar.id, date, "Today")
                    } else {
                        link(call, calendar, date)
                    }
                }
                td {
                    showEvent(call, state, event)
                }
            }
        }
    }
}

private fun TD.showEvent(
    call: ApplicationCall,
    state: State,
    event: Event<*>,
) = when (event) {
    is StartEvent<*> -> displayEvent(
        call, state,
        event,
        getStartText(event)
    )

    is EndEvent<*> -> displayEvent(
        call, state,
        event,
        getEndText(event)
    )

    is OwnershipChangedEvent<*> -> handleOwnershipChanged(call, state, event)
}

private fun getStartText(event: StartEvent<*>): String = when (event.id) {
    is BuildingId -> "was constructed"
    is BusinessId -> "opened"
    is CharacterId -> "was born"
    is FontId, is RaceId, is SpellId -> "was created"
    is PeriodicalId -> "started publishing"
    is OrganizationId, is TownId -> "was founded"
    is TextId -> "was published"
    else -> "started"
}

private fun getEndText(event: EndEvent<*>): String = when (event.id) {
    is CharacterId -> "died"
    else -> "ended"
}

private fun <ID : Id<ID>> HtmlBlockTag.displayEvent(
    call: ApplicationCall,
    state: State,
    event: Event<ID>,
    text: String,
) {
    +"The ${event.id.type()} "
    link(call, state, event.id)
    +" $text."
}

private fun <ID : Id<ID>> HtmlBlockTag.handleOwnershipChanged(
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