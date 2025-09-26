package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdatePersonalityTrait
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_PERSONALITY_TRAIT: Reducer<UpdatePersonalityTrait, State> = { state, action ->
    state.getPersonalityTraitStorage().require(action.trait.id)

    noFollowUps(state.updateStorage(state.getPersonalityTraitStorage().update(action.trait)))
}