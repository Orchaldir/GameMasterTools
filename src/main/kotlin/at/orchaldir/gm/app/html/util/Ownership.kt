package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.character.parseCharacterId
import at.orchaldir.gm.app.html.economy.parseBusinessId
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.organization.parseOrganizationId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseTownId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.organization.getExistingOrganizations
import at.orchaldir.gm.core.selector.realm.*
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
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
    val previousBuildings = getPreviouslyOwned(state.getBuildingStorage(), owner)
    val businesses = getOwned(state.getBusinessStorage(), owner)
    val previousBusinesses = getPreviouslyOwned(state.getBusinessStorage(), owner)
    val periodicals = getOwned(state.getPeriodicalStorage(), owner)
    val previousPeriodicals = getPreviouslyOwned(state.getPeriodicalStorage(), owner)
    val realms = state.sortRealms(state.getSubRealms(owner))
    val previousRealms = state.sortRealms(state.getPreviousSubRealms(owner))
    val towns = state.sortTowns(state.getOwnedTowns(owner))
    val previousTowns = state.sortTowns(state.getPreviousOwnedTowns(owner))

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
    ownership: History<Owner>,
) = showHistory(call, state, ownership, "Owner", HtmlBlockTag::showOwner)

fun HtmlBlockTag.showOwner(
    call: ApplicationCall,
    state: State,
    owner: Owner,
    showUndefined: Boolean = true,
) {
    when (owner) {
        NoOwner -> +"None"
        is OwnedByBusiness -> link(call, state, owner.business)
        is OwnedByCharacter -> link(call, state, owner.character)
        is OwnedByOrganization -> link(call, state, owner.organization)
        is OwnedByRealm -> link(call, state, owner.realm)
        is OwnedByTown -> link(call, state, owner.town)
        UndefinedOwner -> if (showUndefined) {
            +"Undefined"
        }

    }
}

// edit

fun FORM.selectOwnership(
    state: State,
    ownership: History<Owner>,
    startDate: Date?,
) = selectHistory(state, OWNER, ownership, startDate, "Owner", HtmlBlockTag::selectOwner)

fun HtmlBlockTag.selectOwner(
    state: State,
    param: String,
    owner: Owner,
    start: Date?,
) {
    selectValue("Owner Type", param, OwnerType.entries, owner.getType())

    when (owner) {
        is OwnedByBusiness -> selectElement(
            state,
            "Owner",
            combine(param, BUSINESS),
            state.getExistingElements(state.getBusinessStorage(), start),
            owner.business,
        )

        is OwnedByCharacter -> selectElement(
            state,
            "Owner",
            combine(param, CHARACTER),
            state.getLiving(start),
            owner.character,
        )

        is OwnedByOrganization -> selectElement(
            state,
            "Owner",
            combine(param, ORGANIZATION),
            state.getExistingOrganizations(start),
            owner.organization,
        )

        is OwnedByRealm -> selectElement(
            state,
            "Owner",
            combine(param, REALM),
            state.getExistingRealms(start),
            owner.realm,
        )

        is OwnedByTown -> selectElement(
            state,
            "Owner",
            combine(param, TOWN),
            state.getExistingTowns(start),
            owner.town,
        )

        NoOwner, UndefinedOwner -> doNothing()
    }
}

// parsing

fun parseOwnership(parameters: Parameters, state: State, startDate: Date?) =
    parseHistory(parameters, OWNER, state, startDate, ::parseOwner)

private fun parseOwner(parameters: Parameters, state: State, param: String): Owner =
    when (parse(parameters, param, OwnerType.Undefined)) {
        OwnerType.None -> NoOwner
        OwnerType.Business -> OwnedByBusiness(parseBusinessId(parameters, combine(param, BUSINESS)))
        OwnerType.Character -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
        OwnerType.Organization -> OwnedByOrganization(
            parseOrganizationId(parameters, combine(param, ORGANIZATION))
        )

        OwnerType.Realm -> OwnedByRealm(parseRealmId(parameters, combine(param, REALM)))
        OwnerType.Town -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
        OwnerType.Undefined -> UndefinedOwner
    }
