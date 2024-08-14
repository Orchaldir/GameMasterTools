package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateRelationships
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_RELATIONSHIPS: Reducer<UpdateRelationships, State> = { state, action ->
    var character = state.getCharacterStorage().getOrThrow(action.id)
    val removedOthers = character.relationships.keys - action.relationships.keys
    val updated = mutableListOf<Character>()

    action.relationships.forEach { (otherId, relationships) ->
        require(action.id != otherId) { "Relationships must be between 2 different characters!" }
        character = updateRelationships(character, otherId, relationships)

        val other = state.getCharacterStorage().getOrThrow(otherId)
        updated.add(updateRelationships(other, character.id, relationships))
    }

    removedOthers.forEach { otherId ->
        character = updateRelationships(character, otherId, setOf())

        val other = state.getCharacterStorage().getOrThrow(otherId)
        updated.add(updateRelationships(other, character.id, setOf()))
    }

    updated.add(character)

    noFollowUps(state.copy(characters = state.getCharacterStorage().update(updated)))
}

private fun updateRelationships(
    character: Character,
    other: CharacterId,
    relationships: Set<InterpersonalRelationship>,
): Character {
    if (relationships.isEmpty()) {
        return character.copy(relationships = character.relationships - other)
    }

    return character.copy(relationships = character.relationships + mapOf(other to relationships))
}