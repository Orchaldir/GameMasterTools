package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.EvolvedLanguage
import at.orchaldir.gm.core.model.culture.language.InventedLanguage
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.countChildren
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.periodical.countPeriodicals
import at.orchaldir.gm.core.selector.world.countPlanes
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_LANGUAGE: Reducer<CreateLanguage, State> = { state, _ ->
    val language = Language(state.getLanguageStorage().nextId)

    noFollowUps(state.updateStorage(state.getLanguageStorage().add(language)))
}

val DELETE_LANGUAGE: Reducer<DeleteLanguage, State> = { state, action ->
    state.getLanguageStorage().require(action.id)
    validateCanDelete(state.countCharacters(action.id), action.id, "it is known by characters")
    validateCanDelete(state.countChildren(action.id), action.id, "it has children")
    validateCanDelete(state.countPeriodicals(action.id), action.id, "it is used by a periodical")
    validateCanDelete(state.countPlanes(action.id), action.id, "it is used by a plane")
    validateCanDelete(state.countTexts(action.id), action.id, "it is used by a text")

    noFollowUps(state.updateStorage(state.getLanguageStorage().remove(action.id)))
}

val UPDATE_LANGUAGE: Reducer<UpdateLanguage, State> = { state, action ->
    val language = action.language

    state.getLanguageStorage().require(language.id)
    validateLanguage(state, language)

    noFollowUps(state.updateStorage(state.getLanguageStorage().update(language)))
}

fun validateLanguage(state: State, language: Language) {
    checkDate(state, language.startDate(), "Language")
    checkOrigin(state, language)

    // no duplicate name?
    // no circle? (time travel?)
}

private fun checkOrigin(
    state: State,
    language: Language,
) {
    when (val origin = language.origin) {
        is InventedLanguage -> validateCreator(state, origin.inventor, language.id, origin.date, "Inventor")
        is EvolvedLanguage -> {
            require(origin.parent != language.id) { "A language cannot be its own parent!" }
            require(
                state.getLanguageStorage().contains(origin.parent)
            ) { "Cannot use an unknown parent language ${origin.parent.value}!" }
        }

        else -> doNothing()
    }
}