package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.AddRelationship
import at.orchaldir.gm.core.action.RemoveRelationships
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_RELATIONSHIP: Reducer<AddRelationship, State> = { state, action ->
    require(action.id != action.other) { "Relationships must be between 2 different characters!" }

    val character = addRelationship(state, action.id, action.other, action.relationship)
    val other = addRelationship(state, action.other, action.id, action.relationship)

    noFollowUps(state.copy(characters = state.characters.update(listOf(character, other))))
}

val REMOVE_RELATIONSHIPS: Reducer<RemoveRelationships, State> = { state, action ->
    var character = state.characters.getOrThrow(action.id)
    val updated = mutableListOf<Character>()

    action.removed.forEach { (otherId, relationships) ->
        character = removeRelationships(character, otherId, relationships)

        val other = state.characters.getOrThrow(otherId)
        updated.add(removeRelationships(other, character.id, relationships))
    }

    updated.add(character)

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}

private fun addRelationship(
    state: State,
    id: CharacterId,
    other: CharacterId,
    relationship: InterpersonalRelationship,
): Character {
    val character = state.characters.getOrThrow(id)
    val relationships = character.relationships[other] ?: setOf()
    val newRelationships = relationships + relationship

    return character.copy(relationships = character.relationships + mapOf(other to newRelationships))
}

private fun removeRelationships(
    character: Character,
    other: CharacterId,
    relationships: Set<InterpersonalRelationship>,
): Character {
    val relationshipSet = character.relationships[other] ?: return character
    val newRelationshipSet = relationshipSet - relationships

    return character.copy(relationships = character.relationships + mapOf(other to newRelationshipSet))
}