package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.AddLanguage
import at.orchaldir.gm.core.action.RemoveLanguages
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_LANGUAGE: Reducer<AddLanguage, State> = { state, action ->
    state.getLanguageStorage().require(action.language)

    val character = state.getCharacterStorage().getOrThrow(action.id)
    val updated = character.copy(languages = character.languages + mapOf(action.language to action.level))

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}

val REMOVE_LANGUAGES: Reducer<RemoveLanguages, State> = { state, action ->
    action.languages.forEach { state.getLanguageStorage().require(it) }

    val character = state.getCharacterStorage().getOrThrow(action.id)
    val updated = character.copy(languages = character.languages - action.languages)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}
