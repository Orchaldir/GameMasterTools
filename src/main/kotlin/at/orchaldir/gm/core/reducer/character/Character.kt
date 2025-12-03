package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.rpg.CharacterStatblock
import at.orchaldir.gm.core.model.rpg.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.rpg.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_CHARACTERS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_CHARACTERS
import at.orchaldir.gm.core.reducer.rpg.validateStatblock
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.utils.doNothing

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
        character.vitalStatus,
        character.birthDate,
        VALID_VITAL_STATUS_FOR_CHARACTERS,
        VALID_CAUSES_FOR_CHARACTERS,
    )
    checkBeliefStatusHistory(state, character.beliefStatus, character.birthDate)
    checkPositionHistory(state, character.housingStatus, character.birthDate, ALLOWED_HOUSING_TYPES)
    checkEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)
    checkAuthenticity(state, character.authenticity)
    state.getCharacterTraitStorage().require(character.personality)
    validateCharacterStatblock(state, character.statblock)
}

fun validateCharacterStatblock(
    state: State,
    statblock: CharacterStatblock,
) {
    when (statblock) {
        UndefinedCharacterStatblock -> doNothing()
        is UniqueCharacterStatblock -> validateStatblock(state, statblock.statblock)
        is UseStatblockOfTemplate -> state.getCharacterTemplateStorage().require(statblock.template)
    }
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
    validateDate(state, character.birthDate, "Birthday")
    validateOrigin(state, character.id, character.origin, character.birthDate, ::CharacterId)
}
