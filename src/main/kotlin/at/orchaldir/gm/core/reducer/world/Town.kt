package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import at.orchaldir.gm.utils.update

val CREATE_TOWN: Reducer<CreateTown, State> = { state, _ ->
    val moon = Town(state.getTownStorage().nextId)

    noFollowUps(state.updateStorage(state.getTownStorage().add(moon)))
}

val DELETE_TOWN: Reducer<DeleteTown, State> = { state, action ->
    state.getTownStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getTownStorage().remove(action.id)))
}

val UPDATE_TOWN: Reducer<UpdateTown, State> = { state, action ->
    state.getTownStorage().require(action.town.id)

    noFollowUps(state.updateStorage(state.getTownStorage().update(action.town)))
}

val SET_TERRAIN_TILE: Reducer<SetTerrainTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)

    require(oldTown.map.isInside(action.tileIndex)) { "Tile ${action.tileIndex} is outside the map" }

    val terrain = when (action.terrainType) {
        TerrainType.Hill -> {
            val mountainId = MountainId(action.terrainId)
            state.getMountainStorage().require(mountainId)
            HillTerrain(mountainId)
        }

        TerrainType.Mountain -> {
            val mountainId = MountainId(action.terrainId)
            state.getMountainStorage().require(mountainId)
            MountainTerrain(mountainId)
        }

        TerrainType.Plain -> PlainTerrain
        TerrainType.River -> {
            val riverId = RiverId(action.terrainId)
            state.getRiverStorage().require(riverId)
            RiverTerrain(riverId)
        }
    }
    val tile = oldTown.map.tiles[action.tileIndex].copy(terrain = terrain)
    val tiles = oldTown.map.tiles.update(action.tileIndex, tile)
    val town = oldTown.copy(map = oldTown.map.copy(tiles = tiles))

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}