package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.utils.map.Resize
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN: Reducer<CreateTown, State> = { state, _ ->
    val town = Town(state.getTownStorage().nextId, foundingDate = state.time.currentDate)

    noFollowUps(state.updateStorage(state.getTownStorage().add(town)))
}

val DELETE_TOWN: Reducer<DeleteTown, State> = { state, action ->
    state.getTownStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getTownStorage().remove(action.id)))
}

val UPDATE_TOWN: Reducer<UpdateTown, State> = { state, action ->
    state.getTownStorage().require(action.town.id)

    noFollowUps(state.updateStorage(state.getTownStorage().update(action.town)))
}

// town's streets

val ADD_STREET_TILE: Reducer<AddStreetTile, State> = { state, action ->
    state.getStreetStorage().require(action.street)

    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.build(action.tileIndex, StreetTile(action.street))

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

val REMOVE_STREET_TILE: Reducer<RemoveStreetTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.removeStreet(action.tileIndex)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

// town's terrain

val SET_TERRAIN_TILE: Reducer<SetTerrainTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val terrain = createTerrain(state, action.terrainType, action.terrainId)
    val newTown = oldTown.setTerrain(action.tileIndex, terrain)

    noFollowUps(state.updateStorage(state.getTownStorage().update(newTown)))
}

val RESIZE_TERRAIN: Reducer<ResizeTown, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val terrain = createTerrain(state, action.terrainType, action.terrainId)
    val tile = TownTile(terrain)

    val newMap = oldTown.map.resize(action.resize, tile)
    val newTown = oldTown.copy(map = newMap)
    val newBuildings = state.getBuildingStorage().getAll()
        .filter { it.lot.town == action.town }
        .map { building ->
            val newIndex = oldTown.map.getIndexAfterResize(building.lot.tileIndex, action.resize)
            building.copy(lot = building.lot.copy(tileIndex = newIndex))
        }

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().update(newBuildings),
                state.getTownStorage().update(newTown),
            )
        )
    )
}

private fun createTerrain(
    state: State,
    terrainType: TerrainType,
    terrainId: Int,
) = when (terrainType) {
    TerrainType.Hill -> {
        val mountainId = MountainId(terrainId)
        state.getMountainStorage().require(mountainId)
        HillTerrain(mountainId)
    }

    TerrainType.Mountain -> {
        val mountainId = MountainId(terrainId)
        state.getMountainStorage().require(mountainId)
        MountainTerrain(mountainId)
    }

    TerrainType.Plain -> PlainTerrain
    TerrainType.River -> {
        val riverId = RiverId(terrainId)
        state.getRiverStorage().require(riverId)
        RiverTerrain(riverId)
    }
}