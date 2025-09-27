package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.core.action.Action
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
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.utils.redux.noFollowUps
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun updateCulture(state: State, culture: Culture): Pair<State, List<Action>> {
    val oldCulture = state.getCultureStorage().getOrThrow(culture.id)

    culture.validate(state)

    return if (requiresChangeToMononym(culture, oldCulture)) {
        logger.info { "Change names to mononym for Culture ${oldCulture.id.value}" }
        changeNames(state, oldCulture, culture) { changeToMononym(it) }
    } else if (requiresChangeToGenonym(culture, oldCulture)) {
        logger.info { "Change names to genonym for Culture ${oldCulture.id.value}" }
        changeNames(state, oldCulture, culture) { changeToGenonym(it) }
    } else {
        noFollowUps(state.updateStorage(state.getCultureStorage().update(culture)))
    }
}

private fun changeNames(
    state: State,
    oldCulture: Culture,
    culture: Culture,
    converter: (Character) -> Character,
): Pair<State, List<Action>> {
    val updatedCharacters = state.getCharacters(oldCulture.id)
        .map { converter(it) }

    return noFollowUps(
        state.updateStorage(
            listOf(
                state.getCharacterStorage().update(updatedCharacters),
                state.getCultureStorage().update(culture),
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