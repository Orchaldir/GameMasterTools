package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.reducer.rpg.validateStatblockLookup
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.utils.doNothing

fun validateCharacterData(
    state: State,
    character: Character,
) {
    val race = state.getRaceStorage().getOrThrow(character.race)

    state.getCultureStorage().requireOptional(character.culture)
    state.getTitleStorage().requireOptional(character.title)
    validateAge(state, character, race)

    val birthdate = character.startDate(state)

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

private fun validateAge(state: State, character: Character, race: Race) {
    when (character.age) {
        is AgeViaBirthdate -> doNothing()
        AgeViaDefaultLifeStage -> require(race.lifeStages !is ImmutableLifeStage) { "Age via default life stage is not supported by ${race.id.print()}!" }
        is AgeViaLifeStage -> {
            require(race.lifeStages !is ImmutableLifeStage) { "Age via life stage is not supported by ${race.id.print()}!" }
            require(character.age.lifeStage.value in 0..<race.lifeStages.countLifeStages()) {
                "Age via ${character.age.lifeStage.print()} is not supported by ${race.id.print()}!"
            }
        }
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
    val birthdate = character.startDate(state)

    if (character.age is AgeViaBirthdate) {
        validateDate(state, character.age.date, "Birthday")
    }

    validateOrigin(state, character.id, character.origin, birthdate, ::CharacterId)
}
