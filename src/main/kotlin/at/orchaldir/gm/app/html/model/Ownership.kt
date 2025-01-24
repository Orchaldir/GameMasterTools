package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseCharacterId
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.getLiving
import at.orchaldir.gm.core.selector.world.getExistingTowns
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

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
        UndefinedOwner -> if (showUndefined) {
            +"Undefined"
        }
    }
}

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
        is OwnedByCharacter -> selectValue(
            "Owner",
            combine(param, CHARACTER),
            state.getLiving(start),
            false
        ) { character ->
            label = character.name(state)
            value = character.id.value.toString()
            selected = owner.character == character.id
        }

        is OwnedByTown -> selectValue(
            "Owner",
            combine(param, TOWN),
            state.getExistingTowns(start),
            false
        ) { town ->
            label = town.name(state)
            value = town.id.value.toString()
            selected = owner.town == town.id
        }

        else -> doNothing()
    }
}

fun parseOwnership(parameters: Parameters, state: State, startDate: Date?) =
    parseHistory(parameters, OWNER, state, startDate, ::parseOwner)

private fun parseOwner(parameters: Parameters, state: State, param: String): Owner = when (parameters[param]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UndefinedOwner
}
