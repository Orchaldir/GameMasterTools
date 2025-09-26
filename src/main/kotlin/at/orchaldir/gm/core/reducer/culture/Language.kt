package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOrigin
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_LANGUAGE: Reducer<UpdateLanguage, State> = { state, action ->
    val language = action.language

    state.getLanguageStorage().require(language.id)
    validateLanguage(state, language)

    noFollowUps(state.updateStorage(state.getLanguageStorage().update(language)))
}

fun validateLanguage(state: State, language: Language) {
    checkDate(state, language.startDate(), "Language")
    checkOrigin(state, language.id, language.origin, language.date, ::LanguageId)

    // no duplicate name?
    // no circle? (time travel?)
}
