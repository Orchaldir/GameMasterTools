package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingLot
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import at.orchaldir.gm.utils.update

val ADD_BUILDING: Reducer<AddBuilding, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)

    val oldTile = oldTown.map.getRequiredTile(action.tileIndex)

    require(oldTile.canBuild()) { "Tile ${action.tileIndex} is not empty!" }

    val lot = BuildingLot(action.town, action.tileIndex, action.size)
    val building = Building(state.getBuildingStorage().nextId, lot = lot)

    val tile = oldTile.copy(construction = BuildingTile(building.id))
    val tiles = oldTown.map.tiles.update(action.tileIndex, tile)
    val town = oldTown.copy(map = oldTown.map.copy(tiles = tiles))

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
    state.getBuildingStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getBuildingStorage().remove(action.id)))
}