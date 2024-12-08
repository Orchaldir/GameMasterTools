package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.reducer.util.checkCreated
import at.orchaldir.gm.core.reducer.util.checkEmploymentStatusHistory
import at.orchaldir.gm.core.reducer.util.checkHousingStatusHistory
import at.orchaldir.gm.core.selector.economy.getOwnedBusinesses
import at.orchaldir.gm.core.selector.economy.getPreviouslyOwnedBusinesses
import at.orchaldir.gm.core.selector.getChildren
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.getParents
import at.orchaldir.gm.core.selector.world.getOwnedBuildings
import at.orchaldir.gm.core.selector.world.getPreviouslyOwnedBuildings
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.getCharacterStorage().nextId, birthDate = state.time.currentDate)
    val characters = state.getCharacterStorage().add(character)
    noFollowUps(state.updateStorage(characters))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    state.getCharacterStorage().require(action.id)

    val parents = state.getParents(action.id)
    require(parents.isEmpty()) { "Cannot delete character ${action.id.value}, because he has parents!" }
    val children = state.getChildren(action.id)
    require(children.isEmpty()) { "Cannot delete character ${action.id.value}, because he has children!" }

    checkBuildingOwnership(state, action.id)
    checkBusinessOwnership(state, action.id)
    checkCreated(state, action.id, "character")

    noFollowUps(state.updateStorage(state.getCharacterStorage().remove(action.id)))
}

private fun checkBuildingOwnership(state: State, id: CharacterId) {
    val owned = state.getOwnedBuildings(id)
    require(owned.isEmpty()) { "Cannot delete character ${id.value}, because he owns buildings!" }
    val previouslyOwned = state.getPreviouslyOwnedBuildings(id)
    require(previouslyOwned.isEmpty()) { "Cannot delete character ${id.value}, because he previously owned buildings!" }
}

private fun checkBusinessOwnership(state: State, id: CharacterId) {
    val owned = state.getOwnedBusinesses(id)
    require(owned.isEmpty()) { "Cannot delete character ${id.value}, because he owns businesses!" }
    val previouslyOwned = state.getPreviouslyOwnedBusinesses(id)
    require(previouslyOwned.isEmpty()) { "Cannot delete character ${id.value}, because he previously owned businesses!" }
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = action.character
    val oldCharacter = state.getCharacterStorage().getOrThrow(character.id)

    state.getRaceStorage().require(character.race)
    state.getCultureStorage().require(character.culture)
    checkOrigin(state, character)
    checkCauseOfDeath(state, character)
    checkHousingStatusHistory(state, character.housingStatus, character.birthDate)
    checkEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)
    character.personality.forEach { state.getPersonalityTraitStorage().require(it) }
    val update = character.copy(languages = oldCharacter.languages)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(update)))
}

private fun checkOrigin(
    state: State,
    character: Character,
) {
    val calendar = state.getDefaultCalendar()
    require(calendar.isAfterOrEqual(state.time.currentDate, character.birthDate)) { "Character is born in the future!" }

    when (val origin = character.origin) {
        is Born -> {
            val storage = state.getCharacterStorage()
            storage.require(origin.mother) { "Cannot use an unknown mother ${origin.mother.value}!" }
            require(storage.getOrThrow(origin.mother).gender == Gender.Female) { "Mother ${origin.mother.value} is not female!" }
            storage.require(origin.father) { "Cannot use an unknown father ${origin.father.value}!" }
            require(storage.getOrThrow(origin.father).gender == Gender.Male) { "Father ${origin.father.value} is not male!" }
        }

        else -> doNothing()
    }
}

private fun checkCauseOfDeath(
    state: State,
    character: Character,
) {
    if (character.vitalStatus is Dead) {
        val calendar = state.getDefaultCalendar()
        val dead = character.vitalStatus

        dead.deathDay.let {
            require(calendar.isAfterOrEqual(state.time.currentDate, it)) { "Character died in the future!" }
            require(calendar.isAfterOrEqual(it, character.birthDate)) { "Character died before its origin!" }
        }

        if (dead.cause is Murder) {
            state.getCharacterStorage()
                .require(dead.cause.killer) { "Cannot use an unknown killer ${dead.cause.killer}!" }
        }
    }
}


