package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.reducer.rpg.validateStatblockLookup
import at.orchaldir.gm.core.reducer.util.*

fun validateCharacterData(
    state: State,
    character: Character,
) {
    state.getRaceStorage().require(character.race)
    state.getCultureStorage().requireOptional(character.culture)
    state.getTitleStorage().requireOptional(character.title)
    checkSexualOrientation(character)
    checkOrigin(state, character)
    validateVitalStatus(
        state,
        character.id,
        character.status,
        character.date,
        ALLOWED_VITAL_STATUS_FOR_CHARACTER,
        ALLOWED_CAUSES_OF_DEATH_FOR_CHARACTER,
    )
    checkBeliefStatusHistory(state, character.beliefStatus, character.date)
    checkPositionHistory(state, character.housingStatus, character.date, ALLOWED_HOUSING_TYPES)
    checkEmploymentStatusHistory(state, character.employmentStatus, character.date)
    checkAuthenticity(state, character.authenticity)
    validateStatblockLookup(state, character.race, character.statblock)
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
    validateDate(state, character.date, "Birthday")
    validateOrigin(state, character.id, character.origin, character.date, ::CharacterId)
}
