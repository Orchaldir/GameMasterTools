package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ALLOWED_OWNERS
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.selector.realm.getOwnedSettlements
import at.orchaldir.gm.core.selector.realm.getPreviousOwnedSettlements
import at.orchaldir.gm.core.selector.realm.getPreviousSubRealms
import at.orchaldir.gm.core.selector.realm.getSubRealms
import at.orchaldir.gm.core.selector.util.getOwned
import at.orchaldir.gm.core.selector.util.getPreviouslyOwned
import at.orchaldir.gm.utils.Id
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun <ID : Id<ID>> HtmlBlockTag.showOwnedElements(
    call: ApplicationCall,
    state: State,
    owner: ID,
    alwaysShowTitle: Boolean = false,
) {
    val buildings = getOwned(state.getBuildingStorage(), owner)
    val previousBuildings = getPreviouslyOwned(state.getBuildingStorage(), owner) - buildings
    val businesses = getOwned(state.getBusinessStorage(), owner)
    val previousBusinesses = getPreviouslyOwned(state.getBusinessStorage(), owner) - businesses
    val periodicals = getOwned(state.getPeriodicalStorage(), owner)
    val previousPeriodicals = getPreviouslyOwned(state.getPeriodicalStorage(), owner) - periodicals
    val realms = state.getSubRealms(owner)
    val previousRealms = state.getPreviousSubRealms(owner) - realms
    val settlements = state.getOwnedSettlements(owner)
    val previousSettlements = state.getPreviousOwnedSettlements(owner) - settlements

    if (!alwaysShowTitle &&
        buildings.isEmpty() && previousBuildings.isEmpty() &&
        businesses.isEmpty() && previousBusinesses.isEmpty() &&
        periodicals.isEmpty() && previousPeriodicals.isEmpty() &&
        realms.isEmpty() && previousRealms.isEmpty() &&
        settlements.isEmpty() && previousSettlements.isEmpty()
    ) {
        return
    }

    h2 { +"Possession" }

    fieldElements(call, state, "Owned Buildings", buildings)
    fieldElements(call, state, "Previously owned Buildings", previousBuildings)
    fieldElements(call, state, "Owned Businesses", businesses)
    fieldElements(call, state, "Previously owned Businesses", previousBusinesses)
    fieldElements(call, state, "Owned Periodicals", periodicals)
    fieldElements(call, state, "Previously owned Periodicals", previousPeriodicals)
    fieldElements(call, state, "Realms", realms)
    fieldElements(call, state, "Previous Realms", previousRealms)
    fieldElements(call, state, "Settlements", settlements)
    fieldElements(call, state, "Previous Settlements", previousSettlements)
}


fun HtmlBlockTag.showOwnership(
    call: ApplicationCall,
    state: State,
    ownership: History<Reference>,
) = showHistory(call, state, ownership, "Owner", HtmlBlockTag::showReference)

// edit

fun HtmlBlockTag.selectOwnership(
    state: State,
    ownership: History<Reference>,
    startDate: Date?,
) = selectHistory(state, OWNER, ownership, "Owner", startDate, null) { state, param, owner, date ->
    selectReference(state, "Owner", owner, date, param, ALLOWED_OWNERS)
}

// parsing

fun parseOwnership(parameters: Parameters, state: State, startDate: Date?) =
    parseHistory(parameters, OWNER, state, startDate) { parameters, state, param ->
        parseReference(parameters, param)
    }
