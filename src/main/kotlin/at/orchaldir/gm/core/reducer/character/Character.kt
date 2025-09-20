package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_CHARACTERS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_CHARACTERS
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.getCharacterStorage().nextId, birthDate = state.getCurrentDate())
    val characters = state.getCharacterStorage().add(character)
    noFollowUps(state.updateStorage(characters))
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = action.character

    validateCharacterData(state, character)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(character)))
}

fun validateCharacter(
    state: State,
    character: Character,
) {
    validateCharacterData(state, character)
    validateCharacterAppearance(state, character.appearance, character.race)
    validateCharacterEquipment(state, character.equipmentMap)
    state.getDataSourceStorage().require(character.sources)
}

fun validateCharacterData(
    state: State,
    character: Character,
) {
    state.getRaceStorage().require(character.race)
    state.getCultureStorage().requireOptional(character.culture)
    state.getTitleStorage().requireOptional(character.title)
    checkSexualOrientation(character)
    checkOrigin(state, character)
    checkVitalStatus(
        state,
        character.id,
        character.vitalStatus,
        character.birthDate,
        VALID_VITAL_STATUS_FOR_CHARACTERS,
        VALID_CAUSES_FOR_CHARACTERS,
    )
    checkBeliefStatusHistory(state, character.beliefStatus, character.birthDate)
    checkPositionHistory(state, character.housingStatus, character.birthDate, ALLOWED_HOUSING_TYPES)
    checkEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)
    checkAuthenticity(state, character.authenticity)
    character.personality.forEach { state.getPersonalityTraitStorage().require(it) }
}

private fun checkSexualOrientation(character: Character) {
    if (character.gender == Gender.Genderless) {
        require(SEXUAL_ORIENTATION_FOR_GENDERLESS.contains(character.sexuality)) {
            "Sexual orientation ${character.sexuality} is invalid for gender Genderless!"
        }
    }
}

private fun checkOrigin(
    state: State,
    character: Character,
) {
    checkDate(state, character.birthDate, "Birthday")
    checkOrigin(state, character.id, character.origin, character.birthDate, ::CharacterId)
}
