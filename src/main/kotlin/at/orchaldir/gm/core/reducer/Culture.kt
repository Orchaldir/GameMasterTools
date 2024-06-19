package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Genonym
import at.orchaldir.gm.core.model.character.Mononym
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.name.FamilyConvention
import at.orchaldir.gm.core.model.culture.name.MononymConvention
import at.orchaldir.gm.core.model.culture.name.NoNamingConvention
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CULTURE: Reducer<CreateCulture, State> = { state, _ ->
    val culture = Culture(state.cultures.nextId)

    noFollowUps(state.copy(cultures = state.cultures.add(culture)))
}

val DELETE_CULTURE: Reducer<DeleteCulture, State> = { state, action ->
    state.cultures.require(action.id)
    require(state.canDelete(action.id)) { "Culture ${action.id.value} is used by characters" }

    noFollowUps(state.copy(cultures = state.cultures.remove(action.id)))
}

val UPDATE_CULTURE: Reducer<UpdateCulture, State> = { state, action ->
    state.cultures.require(action.culture.id)
    action.culture.namingConvention.getNameLists()
        .forEach { state.nameLists.require(it) }
    val oldCulture = state.cultures.getOrThrow(action.culture.id)

    if (requiresChangeToMononym(action.culture, oldCulture)) {
        val updatedCharacters = state.getCharacters(oldCulture.id)
            .map { changeToMononym(it) }

        noFollowUps(
            state.copy(
                characters = state.characters.update(updatedCharacters),
                cultures = state.cultures.update(action.culture)
            )
        )
    } else {
        noFollowUps(state.copy(cultures = state.cultures.update(action.culture)))
    }
}

private fun requiresChangeToMononym(new: Culture, old: Culture): Boolean {
    if (new.namingConvention is MononymConvention || new.namingConvention is NoNamingConvention) {
        return old.namingConvention is FamilyConvention
    }

    return false
}

private fun changeToMononym(character: Character): Character {
    return when (character.name) {
        is FamilyName -> character.copy(name = Mononym(character.name.given))
        is Genonym -> character.copy(name = Mononym(character.name.given))
        is Mononym -> character
    }
}