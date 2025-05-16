package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.fieldCurrentDate
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
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.getEvents
import at.orchaldir.gm.core.selector.sort
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.*

fun HTML.showEvents(call: ApplicationCall, calendarId: CalendarId) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents(calendar)

    simpleHtml("Events") {
        fieldLink("Calendar", call, state, calendar)
        fieldCurrentDate(call, state)
        showEvents(events, call, state, calendar)
        back("/")
    }
}

fun HtmlBlockTag.showEvents(
    unsortedEvents: List<Event<*>>,
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val events = unsortedEvents.sort(calendar)
    val currentDate = state.getCurrentDate()

    table {
        tr {
            th { +"Date" }
            th { +"Event" }
        }
        events.forEach { event ->
            tr {
                td {
                    val date = event.date

                    if (date is Day && date == currentDate) {
                        link(call, calendar.id, date, "Today")
                    } else {
                        link(call, state, calendar, date)
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
        call,
        state,
        event,
        getStartText(event),
    )

    is EndEvent<*> -> displayEvent(
        call,
        state,
        event,
        getEndText(event),
    )

    is SameStartAndEndEvent<*> -> displayEvent(
        call,
        state,
        event,
        "happened",
    )

    is HistoryEvent<*, *> -> handleHistoricEvent(call, state, event)
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

private fun <ID : Id<ID>> HtmlBlockTag.handleHistoricEvent(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID, *>,
) {
    when (event.type) {
        HistoryEventType.Capital -> handleCapitalChanged(call, state, event as HistoryEvent<ID, TownId?>)
        HistoryEventType.OwnerRealm -> handleRealmOwnershipChanged(call, state, event as HistoryEvent<ID, RealmId?>)
        HistoryEventType.Ownership -> handleOwnershipChanged(call, state, event as HistoryEvent<ID, Owner>)
    }
}

private fun <ID : Id<ID>> HtmlBlockTag.handleCapitalChanged(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID, TownId?>,
) {
    if (event.from != null && event.to != null) {
        link(call, state, event.id)
        +"'s capital changed from "
        link(call, state, event.from)
        +" to "
        link(call, state, event.to)
        +"."
    } else if (event.from != null) {
        link(call, state, event.id)
        +" lost its capital."
    } else if (event.to != null) {
        link(call, state, event.id)
        +" gains "
        link(call, state, event.to)
        +" as capital."
    }
}

private fun <ID : Id<ID>> HtmlBlockTag.handleRealmOwnershipChanged(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID, RealmId?>,
) {
    if (event.from != null && event.to != null) {
        link(call, state, event.id)
        +"'s owner changed from "
        link(call, state, event.from)
        +" to "
        link(call, state, event.to)
        +"."
    } else if (event.from != null) {
        link(call, state, event.id)
        +" becomes independent."
    } else if (event.to != null) {
        link(call, state, event.id)
        +" joins "
        link(call, state, event.to)
        +"."
    }
}

private fun <ID : Id<ID>> HtmlBlockTag.handleOwnershipChanged(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID, Owner>,
) {
    link(call, state, event.id)
    +"'s owner changed from "
    showOwner(call, state, event.from)
    +" to "
    showOwner(call, state, event.to)
    +"."
}