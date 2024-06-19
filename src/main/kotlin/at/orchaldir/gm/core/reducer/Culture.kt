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
import at.orchaldir.gm.core.model.culture.name.isAnyGenonym
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
        changeNames(state, oldCulture, action) { changeToMononym(it) }
    } else if (requiresChangeToGenonym(action.culture, oldCulture)) {
        changeNames(state, oldCulture, action) { changeToGenonym(it) }
    } else {
        noFollowUps(state.copy(cultures = state.cultures.update(action.culture)))
    }
}

private fun changeNames(
    state: State,
    oldCulture: Culture,
    action: UpdateCulture,
    converter: (Character) -> Character,
): Pair<State, List<UpdateCulture>> {
    val updatedCharacters = state.getCharacters(oldCulture.id)
        .map { converter(it) }

    return noFollowUps(
        state.copy(
            characters = state.characters.update(updatedCharacters),
            cultures = state.cultures.update(action.culture)
        )
    )
}

private fun requiresChangeToMononym(new: Culture, old: Culture): Boolean {
    if (new.namingConvention is MononymConvention || new.namingConvention is NoNamingConvention) {
        return old.namingConvention is FamilyConvention || old.namingConvention.isAnyGenonym()
    } else if (new.namingConvention is FamilyConvention) {
        return old.namingConvention.isAnyGenonym()
    }

    return false
}

private fun requiresChangeToGenonym(new: Culture, old: Culture): Boolean {
    if (new.namingConvention.isAnyGenonym()) {
        return !old.namingConvention.isAnyGenonym()
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

private fun changeToGenonym(character: Character): Character {
    return when (character.name) {
        is FamilyName -> character.copy(name = Genonym(character.name.given))
        is Genonym -> character
        is Mononym -> character.copy(name = Genonym(character.name.name))
    }
}