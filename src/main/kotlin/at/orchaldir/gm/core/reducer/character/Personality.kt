package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreatePersonalityTrait
import at.orchaldir.gm.core.action.DeletePersonalityTrait
import at.orchaldir.gm.core.action.UpdatePersonalityTrait
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERSONALITY_TRAIT: Reducer<CreatePersonalityTrait, State> = { state, _ ->
    val personalityTrait = PersonalityTrait(state.getPersonalityTraitStorage().nextId)

    noFollowUps(state.updateStorage(state.getPersonalityTraitStorage().add(personalityTrait)))
}

val DELETE_PERSONALITY_TRAIT: Reducer<DeletePersonalityTrait, State> = { state, action ->
    state.getPersonalityTraitStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getPersonalityTraitStorage().remove(action.id)))
}

val UPDATE_PERSONALITY_TRAIT: Reducer<UpdatePersonalityTrait, State> = { state, action ->
    state.getPersonalityTraitStorage().require(action.trait.id)

    noFollowUps(state.updateStorage(state.getPersonalityTraitStorage().update(action.trait)))
}