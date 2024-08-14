package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_APPEARANCE: Reducer<UpdateAppearance, State> = { state, action ->
    val character = state.getCharacterStorage().getOrThrow(action.id)
    val updated = character.copy(appearance = action.appearance)

    noFollowUps(state.copy(characters = state.getCharacterStorage().update(updated)))
}
