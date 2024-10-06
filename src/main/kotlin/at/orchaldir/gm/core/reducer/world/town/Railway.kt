package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.AddRailwayTile
import at.orchaldir.gm.core.action.RemoveRailwayTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_RAILWAY_TILE: Reducer<AddRailwayTile, State> = { state, action ->
    state.getRailwayTypeStorage().require(action.type)

    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.buildRailway(action.tileIndex, action.type, action.connection)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

val REMOVE_RAILWAY_TILE: Reducer<RemoveRailwayTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.removeRailway(action.tileIndex, action.type)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}