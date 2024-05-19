package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreatePersonalityTrait
import at.orchaldir.gm.core.action.DeletePersonalityTrait
import at.orchaldir.gm.core.action.UpdatePersonalityTrait
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERSONALITY_TRAIT: Reducer<CreatePersonalityTrait, State> = { state, _ ->
    val personalityTrait = PersonalityTrait(state.personalityTraits.nextId)

    noFollowUps(state.copy(personalityTraits = state.personalityTraits.add(personalityTrait)))
}

val DELETE_PERSONALITY_TRAIT: Reducer<DeletePersonalityTrait, State> = { state, action ->
    val contains = state.personalityTraits.contains(action.id)
    require(contains) { "Cannot delete an unknown personality trait ${action.id.value}" }

    noFollowUps(state.copy(personalityTraits = state.personalityTraits.remove(action.id)))
}

val UPDATE_PERSONALITY_TRAIT: Reducer<UpdatePersonalityTrait, State> = { state, action ->
    val trait = action.trait

    val contains = state.personalityTraits.contains(trait.id)
    require(contains) { "Cannot update an unknown personalityTrait ${trait.id.value}" }

    noFollowUps(state.copy(personalityTraits = state.personalityTraits.update(trait)))
}