package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.html.util.fieldCurrentDate
import at.orchaldir.gm.app.html.util.showEmploymentStatus
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.UndefinedEmploymentStatus
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.event.*
import at.orchaldir.gm.core.model.util.font.FontId
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
    is BattleId -> "was fought"
    is BuildingId -> "was constructed"
    is BusinessId -> "opened"
    is CharacterId -> "was born"
    is FontId, is RaceId, is SpellId -> "was created"
    is PeriodicalId -> "started publishing"
    is OrganizationId, is RealmId, is SettlementId -> "was founded"
    is TextId -> "was published"
    is TreatyId -> "was signed"
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
        HistoryEventType.Capital -> handleRealmChanged(
            call,
            state,
            event as HistoryEvent<ID, RealmId?>,
            "capital",
            "lost its capital",
            "gains",
            "as capital",
        )

        HistoryEventType.Currency -> handleRealmChanged(
            call,
            state,
            event as HistoryEvent<ID, CurrencyId?>,
            "currency",
            "?",
            "adopts the currency",
        )

        HistoryEventType.Employment -> handleJobChanged(
            call,
            state,
            event as HistoryEvent<ID, EmploymentStatus>,
        )

        HistoryEventType.LegalCode -> handleRealmChanged(
            call,
            state,
            event as HistoryEvent<ID, LegalCodeId?>,
            "legal code",
            "lawless",
            "adopts the legal code",
        )

        HistoryEventType.OwnerRealm -> handleRealmChanged(
            call,
            state,
            event as HistoryEvent<ID, RealmId?>,
            "owner",
            "becomes independent",
            "joins",
        )

        HistoryEventType.Ownership -> handleOwnershipChanged(call, state, event as HistoryEvent<ID, Reference>)
    }
}

private fun <ID0 : Id<ID0>, ID1 : Id<ID1>> HtmlBlockTag.handleRealmChanged(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID0, ID1?>,
    changeNoun: String,
    becomesNull: String,
    notNullVerb: String,
    notNullVerbSuffix: String? = null,
) {
    if (event.from != null && event.to != null) {
        link(call, state, event.id)
        +"'s $changeNoun changed from "
        link(call, state, event.from)
        +" to "
        link(call, state, event.to)
        +"."
    } else if (event.from != null) {
        link(call, state, event.id)
        +" $becomesNull."
    } else if (event.to != null) {
        link(call, state, event.id)
        +" $notNullVerb "
        link(call, state, event.to)
        if (notNullVerbSuffix != null) {
            +" $notNullVerb."
        } else {
            +"."
        }
    }
}

private fun <ID : Id<ID>> HtmlBlockTag.handleJobChanged(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID, EmploymentStatus>,
) {
    val isFromDefined = event.from !is UndefinedEmploymentStatus
    val isToDefined = event.to !is UndefinedEmploymentStatus

    if (isFromDefined && isToDefined) {
        link(call, state, event.id)
        +"'s job changed from "
        showEmploymentStatus(call, state, event.from)
        +" to "
        showEmploymentStatus(call, state, event.to)
        +"."
    } else if (isFromDefined) {
        link(call, state, event.id)
        +" became unemployed."
    } else if (isToDefined) {
        link(call, state, event.id)
        +" became a "
        showEmploymentStatus(call, state, event.to)
        +"."
    } else {
        error("2 following EmploymentStatus are undefined for ${event.id.print()}!")
    }
}

private fun <ID : Id<ID>> HtmlBlockTag.handleOwnershipChanged(
    call: ApplicationCall,
    state: State,
    event: HistoryEvent<ID, Reference>,
) {
    link(call, state, event.id)
    +"'s owner changed from "
    showReference(call, state, event.from)
    +" to "
    showReference(call, state, event.to)
    +"."
}