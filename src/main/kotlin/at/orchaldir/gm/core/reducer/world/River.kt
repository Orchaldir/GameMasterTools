package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_RIVER: Reducer<UpdateRiver, State> = { state, action ->
    state.getRiverStorage().require(action.river.id)

    noFollowUps(state.updateStorage(state.getRiverStorage().update(action.river)))
}