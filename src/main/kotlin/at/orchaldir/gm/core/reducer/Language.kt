package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.reducer.util.checkCreator
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.core.selector.getChildren
import at.orchaldir.gm.core.selector.item.getBooks
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_LANGUAGE: Reducer<CreateLanguage, State> = { state, _ ->
    val language = Language(state.getLanguageStorage().nextId)

    noFollowUps(state.updateStorage(state.getLanguageStorage().add(language)))
}

val DELETE_LANGUAGE: Reducer<DeleteLanguage, State> = { state, action ->
    state.getLanguageStorage().require(action.id)
    require(state.getChildren(action.id).isEmpty()) { "Cannot delete language ${action.id.value} with children!" }
    require(
        state.getCharacters(action.id).isEmpty()
    ) { "Cannot delete language ${action.id.value} that is known by characters!" }
    require(
        state.getBooks(action.id).isEmpty()
    ) { "Cannot delete language ${action.id.value} that is used by books!" }

    noFollowUps(state.updateStorage(state.getLanguageStorage().remove(action.id)))
}

val UPDATE_LANGUAGE: Reducer<UpdateLanguage, State> = { state, action ->
    val language = action.language

    state.getLanguageStorage().require(language.id)
    checkOrigin(state, language)

    // no duplicate name?
    // no circle? (time travel?)
    noFollowUps(state.updateStorage(state.getLanguageStorage().update(language)))
}

private fun checkOrigin(
    state: State,
    language: Language,
) {
    when (val origin = language.origin) {
        is InventedLanguage -> {
            checkCreator(state, origin.inventor, language.id, origin.date, "Inventor")
        }

        is EvolvedLanguage -> {
            require(origin.parent != language.id) { "A language cannot be its own parent!" }
            require(
                state.getLanguageStorage().contains(origin.parent)
            ) { "Cannot use an unknown parent language ${origin.parent.value}!" }
        }

        else -> doNothing()
    }
}