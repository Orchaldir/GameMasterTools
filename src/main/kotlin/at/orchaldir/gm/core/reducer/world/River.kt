package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateRiver
import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.selector.world.canDeleteRiver
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RIVER: Reducer<CreateRiver, State> = { state, _ ->
    val moon = River(state.getRiverStorage().nextId)

    noFollowUps(state.updateStorage(state.getRiverStorage().add(moon)))
}

val DELETE_RIVER: Reducer<DeleteRiver, State> = { state, action ->
    state.getRiverStorage().require(action.id)

    state.canDeleteRiver(action.id).validate()

    noFollowUps(state.updateStorage(state.getRiverStorage().remove(action.id)))
}

val UPDATE_RIVER: Reducer<UpdateRiver, State> = { state, action ->
    state.getRiverStorage().require(action.river.id)

    noFollowUps(state.updateStorage(state.getRiverStorage().update(action.river)))
}