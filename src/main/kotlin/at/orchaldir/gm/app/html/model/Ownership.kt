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
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.world.exists
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
) {
    when (owner) {
        NoOwner -> +"None"
        is OwnedByCharacter -> link(call, state, owner.character)
        is OwnedByTown -> link(call, state, owner.town)
        UnknownOwner -> +"Unknown"
    }
}

fun FORM.selectOwnership(
    state: State,
    ownership: History<Owner>,
    startDate: Date,
) = selectHistory(state, OWNER, ownership, startDate, "Owners", HtmlBlockTag::selectOwner)

fun HtmlBlockTag.selectOwner(
    state: State,
    param: String,
    owner: Owner,
    start: Date,
) {
    selectValue("Owner Type", param, OwnerType.entries, true) { type ->
        label = type.toString()
        value = type.toString()
        selected = owner.getType() == type
    }
    when (owner) {
        is OwnedByCharacter -> selectValue(
            "Owner",
            combine(param, CHARACTER),
            state.getCharacterStorage().getAll(),
            false
        ) { character ->
            label = character.name(state)
            value = character.id.value.toString()
            selected = owner.character == character.id
            disabled = !state.isAlive(character, start)
        }

        is OwnedByTown -> selectValue(
            "Owner",
            combine(param, TOWN),
            state.getTownStorage().getAll(),
            false
        ) { town ->
            label = town.name(state)
            value = town.id.value.toString()
            selected = owner.town == town.id
            disabled = !state.exists(town, start)
        }

        else -> doNothing()
    }
}

fun parseOwnership(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, OWNER, state, startDate, ::parseOwner)

private fun parseOwner(parameters: Parameters, state: State, param: String): Owner = when (parameters[param]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UnknownOwner
}
