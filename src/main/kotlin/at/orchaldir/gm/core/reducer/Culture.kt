package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CloneCulture
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
import at.orchaldir.gm.core.selector.culture.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

val CREATE_CULTURE: Reducer<CreateCulture, State> = { state, _ ->
    val culture = Culture(state.getCultureStorage().nextId)

    noFollowUps(state.updateStorage(state.getCultureStorage().add(culture)))
}

val CLONE_CULTURE: Reducer<CloneCulture, State> = { state, action ->
    val original = state.getCultureStorage().getOrThrow(action.id)
    val cloneId = state.getCultureStorage().nextId
    val clone = original.copy(id = cloneId, name = "Clone ${cloneId.value}")

    noFollowUps(state.updateStorage(state.getCultureStorage().add(clone)))
}

val DELETE_CULTURE: Reducer<DeleteCulture, State> = { state, action ->
    state.getCultureStorage().require(action.id)
    require(state.canDelete(action.id)) { "Culture ${action.id.value} is used by characters" }

    noFollowUps(state.updateStorage(state.getCultureStorage().remove(action.id)))
}

val UPDATE_CULTURE: Reducer<UpdateCulture, State> = { state, action ->
    state.getCultureStorage().require(action.culture.id)
    state.getCalendarStorage().require(action.culture.calendar)
    action.culture.namingConvention.getNameLists()
        .forEach { state.getNameListStorage().require(it) }
    val oldCulture = state.getCultureStorage().getOrThrow(action.culture.id)

    if (requiresChangeToMononym(action.culture, oldCulture)) {
        logger.info { "Change names to mononym for Culture ${oldCulture.id.value}" }
        changeNames(state, oldCulture, action) { changeToMononym(it) }
    } else if (requiresChangeToGenonym(action.culture, oldCulture)) {
        logger.info { "Change names to genonym for Culture ${oldCulture.id.value}" }
        changeNames(state, oldCulture, action) { changeToGenonym(it) }
    } else {
        noFollowUps(state.updateStorage(state.getCultureStorage().update(action.culture)))
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
        state.updateStorage(
            listOf(
                state.getCharacterStorage().update(updatedCharacters),
                state.getCultureStorage().update(action.culture),
            )
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