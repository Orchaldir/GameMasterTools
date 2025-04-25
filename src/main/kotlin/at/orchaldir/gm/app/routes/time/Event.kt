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
    is StartEvent<*> -> when (val id = event.id) {
        is BuildingId -> displayEvent(call, state, event, "was constructed")
        is BusinessId -> displayEvent(call, state, event, "opened")
        is CharacterId -> displayEvent(call, state, event, "was born")
        is FontId, is RaceId, is SpellId -> displayEvent(call, state, event, "was created")
        is PeriodicalId -> displayEvent(call, state, event, "started publishing")
        is OrganizationId, is TownId -> displayEvent(call, state, event, "was founded")
        is TextId -> displayEvent(call, state, event, "was published")
        else -> displayEvent(call, state, event, "started")
    }

    is EndEvent<*> -> when (val id = event.id) {
        is CharacterId -> displayEvent(call, state, event, "died")
        else -> displayEvent(call, state, event, "ended")
    }

    is OwnershipChangedEvent<*> -> handleOwnershipChanged(call, state, event)
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