package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_LANGUAGE: Reducer<CreateLanguage, State> = { state, _ ->
    val language = Language(state.languages.nextId)

    noFollowUps(state.copy(languages = state.languages.add(language)))
}

val DELETE_LANGUAGE: Reducer<DeleteLanguage, State> = { state, action ->
    noFollowUps(state.copy(languages = state.languages.remove(action.id)))
}

val UPDATE_LANGUAGE: Reducer<UpdateLanguage, State> = { state, action ->
    // id exists
    // no duplicate name
    // parent/inventor exists
    // no circle
    noFollowUps(state.copy(languages = state.languages.update(action.language)))
}