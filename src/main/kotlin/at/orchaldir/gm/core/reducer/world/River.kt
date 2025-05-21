package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateRiver
import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.region.River
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RIVER: Reducer<CreateRiver, State> = { state, _ ->
    val moon = River(state.getRiverStorage().nextId)

    noFollowUps(state.updateStorage(state.getRiverStorage().add(moon)))
}

val DELETE_RIVER: Reducer<DeleteRiver, State> = { state, action ->
    state.getRiverStorage().require(action.id)
    validateCanDelete(state.canDelete(action.id), action.id)

    noFollowUps(state.updateStorage(state.getRiverStorage().remove(action.id)))
}

val UPDATE_RIVER: Reducer<UpdateRiver, State> = { state, action ->
    state.getRiverStorage().require(action.river.id)

    noFollowUps(state.updateStorage(state.getRiverStorage().update(action.river)))
}