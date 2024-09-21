package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingLot
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_BUILDING: Reducer<AddBuilding, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)

    oldTown.map.requireIsInside(action.tileIndex)

    val lot = BuildingLot(action.town, action.tileIndex, action.size)
    val building = Building(state.getBuildingStorage().nextId, lot = lot)

    noFollowUps(state.updateStorage(state.getBuildingStorage().add(building)))
}

val DELETE_BUILDING: Reducer<DeleteBuilding, State> = { state, action ->
    state.getBuildingStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getBuildingStorage().remove(action.id)))
}