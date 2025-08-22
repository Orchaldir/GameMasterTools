package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ALLOWED_OWNERS
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.selector.realm.getOwnedTowns
import at.orchaldir.gm.core.selector.realm.getPreviousOwnedTowns
import at.orchaldir.gm.core.selector.realm.getPreviousSubRealms
import at.orchaldir.gm.core.selector.realm.getSubRealms
import at.orchaldir.gm.core.selector.util.getOwned
import at.orchaldir.gm.core.selector.util.getPreviouslyOwned
import at.orchaldir.gm.core.selector.util.sortRealms
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.utils.Id
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
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
    val realms = state.sortRealms(state.getSubRealms(owner))
    val previousRealms = state.sortRealms(state.getPreviousSubRealms(owner)) - realms
    val towns = state.sortTowns(state.getOwnedTowns(owner))
    val previousTowns = state.sortTowns(state.getPreviousOwnedTowns(owner)) - towns

    if (!alwaysShowTitle &&
        buildings.isEmpty() && previousBuildings.isEmpty() &&
        businesses.isEmpty() && previousBusinesses.isEmpty() &&
        periodicals.isEmpty() && previousPeriodicals.isEmpty() &&
        realms.isEmpty() && previousRealms.isEmpty() &&
        towns.isEmpty() && previousTowns.isEmpty()
    ) {
        return
    }

    h2 { +"Possession" }

    fieldList(call, state, "Owned Buildings", buildings)
    fieldList(call, state, "Previously owned Buildings", previousBuildings)
    fieldList(call, state, "Owned Businesses", businesses)
    fieldList(call, state, "Previously owned Businesses", previousBusinesses)
    fieldList(call, state, "Owned Periodicals", periodicals)
    fieldList(call, state, "Previously owned Periodicals", previousPeriodicals)
    fieldList(call, state, "Realms", realms)
    fieldList(call, state, "Previous Realms", previousRealms)
    fieldList(call, state, "Towns", towns)
    fieldList(call, state, "Previous Towns", previousTowns)
}


fun HtmlBlockTag.showOwnership(
    call: ApplicationCall,
    state: State,
    ownership: History<Reference>,
) = showHistory(call, state, ownership, "Owner", HtmlBlockTag::showReference)

// edit

fun FORM.selectOwnership(
    state: State,
    ownership: History<Reference>,
    startDate: Date?,
) = selectHistory(state, OWNER, ownership, "Owner", startDate, null) { state, param, owner, date ->
    selectReference(state, owner, date, param, ALLOWED_OWNERS)
}

// parsing

fun parseOwnership(parameters: Parameters, state: State, startDate: Date?) =
    parseHistory(parameters, OWNER, state, startDate) { parameters, state, param ->
        parseReference(parameters, param)
    }
