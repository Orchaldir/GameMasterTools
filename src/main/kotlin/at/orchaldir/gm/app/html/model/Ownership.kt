package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.ORGANIZATION
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.organization.parseOrganizationId
import at.orchaldir.gm.app.parse.parseCharacterId
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.economy.getOwnedBusinesses
import at.orchaldir.gm.core.selector.economy.getPreviouslyOwnedBusinesses
import at.orchaldir.gm.core.selector.getLiving
import at.orchaldir.gm.core.selector.organization.getExistingOrganization
import at.orchaldir.gm.core.selector.world.getExistingTowns
import at.orchaldir.gm.core.selector.world.getOwnedBuildings
import at.orchaldir.gm.core.selector.world.getPreviouslyOwnedBuildings
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>> HtmlBlockTag.showOwnedElements(
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    showList("Owned Buildings", state.getOwnedBuildings(id)) { building ->
        link(call, state, building)
    }

    showList("Previously owned Buildings", state.getPreviouslyOwnedBuildings(id)) { building ->
        link(call, state, building)
    }

    showList("Owned Businesses", state.getOwnedBusinesses(id)) { business ->
        link(call, state, business)
    }

    showList("Previously owned Businesses", state.getPreviouslyOwnedBusinesses(id)) { business ->
        link(call, state, business)
    }
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
) = selectHistory(state, OWNER, ownership, startDate, "Owners", HtmlBlockTag::selectOwner)

fun HtmlBlockTag.selectOwner(
    state: State,
    param: String,
    owner: Owner,
    start: Date?,
) {
    selectValue("Owner Type", param, OwnerType.entries, owner.getType(), true)

    when (owner) {
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
            state.getExistingOrganization(start),
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
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Organization.toString() -> OwnedByOrganization(
        parseOrganizationId(
            parameters,
            combine(param, ORGANIZATION)
        )
    )

    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UndefinedOwner
}
