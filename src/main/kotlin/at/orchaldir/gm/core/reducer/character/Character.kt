package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.reducer.rpg.validateStatblockLookup
import at.orchaldir.gm.core.reducer.util.*

fun validateCharacterData(
    state: State,
    character: Character,
) {
    val birthdate = character.startDate(state)

    state.getRaceStorage().require(character.race)
    state.getCultureStorage().requireOptional(character.culture)
    state.getTitleStorage().requireOptional(character.title)
    checkSexualOrientation(character)
    checkOrigin(state, character)
    validateVitalStatus(
        state,
        character.id,
        character.status,
        birthdate,
        ALLOWED_VITAL_STATUS_FOR_CHARACTER,
        ALLOWED_CAUSES_OF_DEATH_FOR_CHARACTER,
    )
    checkBeliefStatusHistory(state, character.beliefStatus, birthdate)
    checkPositionHistory(state, character.housingStatus, birthdate, ALLOWED_HOUSING_TYPES)
    checkEmploymentStatusHistory(state, character.employmentStatus, birthdate)
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
    val birthdate = character.startDate(state)

    if (character.age is AgeViaBirthdate) {
        validateDate(state, character.age.date, "Birthday")
    }

    validateOrigin(state, character.id, character.origin, birthdate, ::CharacterId)
}
