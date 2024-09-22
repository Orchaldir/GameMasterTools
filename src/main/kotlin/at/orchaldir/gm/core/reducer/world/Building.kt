package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingLot
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_BUILDING: Reducer<AddBuilding, State> = { state, action ->
    val buildingId = state.getBuildingStorage().nextId
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.build(action.tileIndex, action.size, BuildingTile(buildingId))
    val lot = BuildingLot(action.town, action.tileIndex, action.size)
    val building = Building(buildingId, lot = lot, constructionDate = state.time.currentDate)

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().add(building),
                state.getTownStorage().update(town),
            )
        )
    )
}

val DELETE_BUILDING: Reducer<DeleteBuilding, State> = { state, action ->
    val building = state.getBuildingStorage().getOrThrow(action.id)
    val oldTown = state.getTownStorage().getOrThrow(building.lot.town)
    val town = oldTown.removeBuilding(building.id)

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().remove(action.id),
                state.getTownStorage().update(town),
            )
        )
    )
}

val UPDATE_BUILDING: Reducer<UpdateBuilding, State> = { state, action ->
    val oldBuilding = state.getBuildingStorage().getOrThrow(action.id)
    val building = oldBuilding.copy(name = action.name, constructionDate = action.constructionDate)

    noFollowUps(state.updateStorage(state.getBuildingStorage().update(building)))
}