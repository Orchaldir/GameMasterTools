package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateRailwayType
import at.orchaldir.gm.core.action.DeleteRailwayType
import at.orchaldir.gm.core.action.UpdateRailwayType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.railway.RailwayType
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RAILWAY_TYPE: Reducer<CreateRailwayType, State> = { state, _ ->
    val type = RailwayType(state.getRailwayTypeStorage().nextId)

    noFollowUps(state.updateStorage(state.getRailwayTypeStorage().add(type)))
}

val DELETE_RAILWAY_TYPE: Reducer<DeleteRailwayType, State> = { state, action ->
    state.getRailwayTypeStorage().require(action.id)
    require(state.canDelete(action.id)) { "Railway Type ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getRailwayTypeStorage().remove(action.id)))
}

val UPDATE_RAILWAY_TYPE: Reducer<UpdateRailwayType, State> = { state, action ->
    state.getRailwayTypeStorage().require(action.type.id)

    noFollowUps(state.updateStorage(state.getRailwayTypeStorage().update(action.type)))
}