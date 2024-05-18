package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageOrigin
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_LANGUAGE: Reducer<CreateLanguage, State> = { state, _ ->
    val language = Language(state.languages.nextId)

    noFollowUps(state.copy(languages = state.languages.add(language)))
}

val DELETE_LANGUAGE: Reducer<DeleteLanguage, State> = { state, action ->
    val contains = state.languages.contains(action.id)
    require(contains) { "Cannot delete an unknown language ${action.id.value}" }
    noFollowUps(state.copy(languages = state.languages.remove(action.id)))
}

val UPDATE_LANGUAGE: Reducer<UpdateLanguage, State> = { state, action ->
    val language = action.language
    val contains = state.languages.contains(language.id)

    require(contains) { "Cannot update an unknown language ${language.id.value}" }
    checkOrigin(state, language.origin)

    // no duplicate name
    // no circle
    noFollowUps(state.copy(languages = state.languages.update(language)))
}

private fun checkOrigin(
    state: State,
    origin: LanguageOrigin,
) {
    when (origin) {
        is InventedLanguage -> {
            require(state.characters.contains(origin.inventor)) { "Cannot use an unknown inventor ${origin.inventor.value}" }
        }

        is EvolvedLanguage -> {
            require(state.languages.contains(origin.parent)) { "Cannot use an unknown parent language ${origin.parent.value}" }
        }

        else -> doNothing()
    }
}