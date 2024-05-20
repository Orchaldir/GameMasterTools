package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.selector.getInventedLanguages
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.characters.nextId)

    noFollowUps(state.copy(characters = state.characters.add(character)))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    state.characters.require(action.id)

    val invented = state.getInventedLanguages(action.id)
    require(invented.isEmpty()) { "Cannot delete a character ${action.id.value}, because he is an language inventor" }

    noFollowUps(state.copy(characters = state.characters.remove(action.id)))
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = action.character

    state.characters.require(character.id)
    state.races.require(character.race)
    character.personality.forEach { state.personalityTraits.require(it) }

    if (character.culture != null) {
        state.cultures.require(character.culture)
    }

    noFollowUps(state.copy(characters = state.characters.update(character)))
}

val ADD_LANGUAGE: Reducer<AddLanguage, State> = { state, action ->
    state.languages.require(action.language)

    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(languages = character.languages + mapOf(action.language to action.level))

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}

val REMOVE_LANGUAGES: Reducer<RemoveLanguages, State> = { state, action ->
    action.languages.forEach { state.languages.require(it) }

    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(languages = character.languages - action.languages)

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}
