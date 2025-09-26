package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.UpdateStreet
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_STREET: Reducer<UpdateStreet, State> = { state, action ->
    state.getStreetStorage().require(action.street.id)

    noFollowUps(state.updateStorage(state.getStreetStorage().update(action.street)))
}