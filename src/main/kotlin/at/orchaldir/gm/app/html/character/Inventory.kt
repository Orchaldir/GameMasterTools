package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.EQUIPPED
import at.orchaldir.gm.app.html.button
import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.selector.culture.hasFashion
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentIdMapForLookup
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HtmlBlockTag

// edit

fun HtmlBlockTag.editInventory(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val generateLink = call.application.href(CharacterRoutes.Inventory.Generate(character.id))

    button("Random", generateLink)
    editEquipped(call, state, EQUIPPED, character.equipped, character.statblock, state.hasFashion(character))
}

// parse

fun parseInventory(
    state: State,
    parameters: Parameters,
    id: CharacterId,
): Character {
    val character = state.getCharacterStorage().getOrThrow(id)
    val baseEquipment = state.getEquipmentIdMapForLookup(character.statblock)
    val equipped = parseEquipped(parameters, state, EQUIPPED, baseEquipment)

    return character.copy(equipped = equipped)
}