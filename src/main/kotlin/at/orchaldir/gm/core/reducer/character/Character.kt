package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Born
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.Murder
import at.orchaldir.gm.core.selector.getChildren
import at.orchaldir.gm.core.selector.getInventedLanguages
import at.orchaldir.gm.core.selector.getParents
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.getCharacterStorage().nextId)
    logger.info { "new character $character" }
    val characters = state.getCharacterStorage().add(character)
    logger.info { "new characters $characters" }
    noFollowUps(state.copy(characters = characters))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    state.getCharacterStorage().require(action.id)

    val invented = state.getInventedLanguages(action.id)
    require(invented.isEmpty()) { "Cannot delete character ${action.id.value}, because he is an language inventor" }
    val parents = state.getParents(action.id)
    require(parents.isEmpty()) { "Cannot delete character ${action.id.value}, because he has parents" }
    val children = state.getChildren(action.id)
    require(children.isEmpty()) { "Cannot delete character ${action.id.value}, because he has children" }

    noFollowUps(state.copy(characters = state.getCharacterStorage().remove(action.id)))
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = action.character
    val oldCharacter = state.getCharacterStorage().getOrThrow(character.id)

    state.getRaceStorage().require(character.race)
    state.getCultureStorage().require(character.culture)
    checkOrigin(state, character)
    checkCauseOfDeath(state, character)
    character.personality.forEach { state.getPersonalityTraitStorage().require(it) }
    val update = character.copy(languages = oldCharacter.languages)

    noFollowUps(state.copy(characters = state.getCharacterStorage().update(update)))
}

private fun checkOrigin(
    state: State,
    character: Character,
) {
    require(character.birthDate <= state.time.currentDate) { "Character is born in the future!" }

    when (val origin = character.origin) {
        is Born -> {
            require(
                state.getCharacterStorage().contains(origin.mother)
            ) { "Cannot use an unknown mother ${origin.mother.value}!" }
            require(
                state.getCharacterStorage().getOrThrow(origin.mother).gender == Gender.Female
            ) { "Mother ${origin.mother.value} is not female!" }
            require(
                state.getCharacterStorage().contains(origin.father)
            ) { "Cannot use an unknown father ${origin.father.value}!" }
            require(
                state.getCharacterStorage().getOrThrow(origin.father).gender == Gender.Male
            ) { "Father ${origin.father.value} is not male!" }
        }

        else -> doNothing()
    }
}

private fun checkCauseOfDeath(
    state: State,
    character: Character,
) {
    character.causeOfDeath.getDeathDate()?.let {
        require(it <= state.time.currentDate) { "Character died in the future!" }
        require(it >= character.birthDate) { "Character died before its origin!" }
    }

    if (character.causeOfDeath is Murder) {
        require(
            state.getCharacterStorage().contains(character.causeOfDeath.killer)
        ) { "Cannot use an unknown killer ${character.causeOfDeath.killer}!" }
    }
}
