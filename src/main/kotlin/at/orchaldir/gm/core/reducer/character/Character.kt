package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.selector.getChildren
import at.orchaldir.gm.core.selector.getInventedLanguages
import at.orchaldir.gm.core.selector.getParents
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.getCharacterStorage().nextId, birthDate = state.time.currentDate)
    logger.info { "new character $character" }
    val characters = state.getCharacterStorage().add(character)
    logger.info { "new characters $characters" }
    noFollowUps(state.updateStorage(characters))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    state.getCharacterStorage().require(action.id)

    val invented = state.getInventedLanguages(action.id)
    require(invented.isEmpty()) { "Cannot delete character ${action.id.value}, because he is an language inventor" }
    val parents = state.getParents(action.id)
    require(parents.isEmpty()) { "Cannot delete character ${action.id.value}, because he has parents" }
    val children = state.getChildren(action.id)
    require(children.isEmpty()) { "Cannot delete character ${action.id.value}, because he has children" }

    noFollowUps(state.updateStorage(state.getCharacterStorage().remove(action.id)))
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

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(update)))
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
    if (character.vitalStatus is Dead) {
        val dead: Dead = character.vitalStatus

        dead.deathDay.let {
            require(it <= state.time.currentDate) { "Character died in the future!" }
            require(it >= character.birthDate) { "Character died before its origin!" }
        }

        if (dead.cause is Murder) {
            require(
                state.getCharacterStorage().contains(dead.cause.killer)
            ) { "Cannot use an unknown killer ${dead.cause.killer}!" }
        }
    }
}
