package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.AddLanguage
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.characters.nextId)

    noFollowUps(state.copy(characters = state.characters.add(character)))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    noFollowUps(state.copy(characters = state.characters.remove(action.id)))
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = Character(action.id, action.name, action.race, action.gender, action.culture)

    noFollowUps(state.copy(characters = state.characters.update(character)))
}

val ADD_LANGUAGE: Reducer<AddLanguage, State> = { state, action ->
    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(languages = character.languages + mapOf(action.language to action.level))

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}