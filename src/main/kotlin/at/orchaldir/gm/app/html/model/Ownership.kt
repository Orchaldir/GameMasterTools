package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.character.parseCharacterId
import at.orchaldir.gm.app.html.model.economy.parseBusinessId
import at.orchaldir.gm.app.html.model.organization.parseOrganizationId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.organization.getExistingOrganizations
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.getOwned
import at.orchaldir.gm.core.selector.util.getPreviouslyOwned
import at.orchaldir.gm.core.selector.world.getExistingTowns
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

    if (!alwaysShowTitle &&
        buildings.isEmpty() && previousBuildings.isEmpty() &&
        businesses.isEmpty() && previousBusinesses.isEmpty() &&
        periodicals.isEmpty() && previousPeriodicals.isEmpty()
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
        is OwnedByTown -> link(call, state, owner.town)
        is OwnedByOrganization -> link(call, state, owner.organization)
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
    selectValue("Owner Type", param, OwnerType.entries, owner.getType(), true)

    when (owner) {
        is OwnedByBusiness -> selectElement(
            state,
            "Owner",
            combine(param, BUSINESS),
            state.getExistingElements(state.getBusinessStorage(), start),
            owner.business,
            false
        )

        is OwnedByCharacter -> selectElement(
            state,
            "Owner",
            combine(param, CHARACTER),
            state.getLiving(start),
            owner.character,
            false
        )

        is OwnedByOrganization -> selectElement(
            state,
            "Owner",
            combine(param, ORGANIZATION),
            state.getExistingOrganizations(start),
            owner.organization,
            false
        )

        is OwnedByTown -> selectElement(
            state,
            "Owner",
            combine(param, TOWN),
            state.getExistingTowns(start),
            owner.town,
            false
        )

        NoOwner, UndefinedOwner -> doNothing()
    }
}

// parsing

fun parseOwnership(parameters: Parameters, state: State, startDate: Date?) =
    parseHistory(parameters, OWNER, state, startDate, ::parseOwner)

private fun parseOwner(parameters: Parameters, state: State, param: String): Owner = when (parameters[param]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Business.toString() -> OwnedByBusiness(parseBusinessId(parameters, combine(param, BUSINESS)))
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Organization.toString() -> OwnedByOrganization(
        parseOrganizationId(parameters, combine(param, ORGANIZATION))
    )

    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UndefinedOwner
}
