package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Born
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.SEXUAL_ORIENTATION_FOR_GENDERLESS
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.character.getChildren
import at.orchaldir.gm.core.selector.character.getParents
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.realm.countBattlesLedBy
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.getCharacterStorage().nextId, birthDate = state.getCurrentDate())
    val characters = state.getCharacterStorage().add(character)
    noFollowUps(state.updateStorage(characters))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    state.getCharacterStorage().require(action.id)

    val parents = state.getParents(action.id)
    val children = state.getChildren(action.id)
    val organizations = state.getOrganizations(action.id)
    validateCanDelete(parents.isEmpty(), action.id, "he has parents")
    validateCanDelete(children.isEmpty(), action.id, "he has children")
    validateCanDelete(organizations.isEmpty(), action.id, "he is a member of an organization")
    validateCanDelete(state.countBattlesLedBy(action.id) == 0, action.id, "of a battle")

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)

    noFollowUps(state.updateStorage(state.getCharacterStorage().remove(action.id)))
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = action.character

    validateCharacterData(state, character)

    val oldCharacter = state.getCharacterStorage().getOrThrow(character.id)
    val update = character.copy(languages = oldCharacter.languages)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(update)))
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
    state.getCultureStorage().require(character.culture)
    state.getTitleStorage().requireOptional(character.title)
    checkSexualOrientation(character)
    checkOrigin(state, character)
    checkVitalStatus(state, character.id, character.vitalStatus, character.birthDate)
    checkBeliefStatusHistory(state, character.beliefStatus, character.birthDate)
    checkHousingStatusHistory(state, character.housingStatus, character.birthDate)
    checkEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)
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

    when (val origin = character.origin) {
        is Born -> {
            val storage = state.getCharacterStorage()
            if (origin.mother != null) {
                storage.require(origin.mother) { "Cannot use an unknown mother ${origin.mother.value}!" }
                require(storage.getOrThrow(origin.mother).gender == Gender.Female) { "Mother ${origin.mother.value} is not female!" }
            }
            if (origin.father != null) {
                storage.requireOptional(origin.father) { "Cannot use an unknown father ${origin.father.value}!" }
                require(storage.getOrThrow(origin.father).gender == Gender.Male) { "Father ${origin.father.value} is not male!" }
            }
        }

        else -> doNothing()
    }
}
