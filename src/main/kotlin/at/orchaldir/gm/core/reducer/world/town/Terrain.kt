package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.ResizeTerrain
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val SET_TERRAIN_TILE: Reducer<SetTerrainTile, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val terrain = createTerrain(state, action.terrainType, action.terrainId)
    val map = oldMap.setTerrain(action.tileIndex, terrain)

    noFollowUps(state.updateStorage(state.getTownMapStorage().update(map)))
}

val RESIZE_TERRAIN: Reducer<ResizeTerrain, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val terrain = createTerrain(state, action.terrainType, action.terrainId)
    val tile = TownTile(terrain)

    val newTileMap = oldMap.map.resize(action.resize, tile)
    val newMap = oldMap.copy(map = newTileMap)
    val newBuildings = state.getBuildingsIn(action.town)
        .map { building ->
            if (building.position is InTownMap) {
                val newIndex = oldMap.map.getIndexAfterResize(building.position.tileIndex, action.resize)

                if (newIndex != null) {
                    building.copy(position = building.position.copy(tileIndex = newIndex))
                } else {
                    error("Resize would remove building ${building.id.value}!")
                }
            } else {
                error("Resize requires InTownMap!")
            }
        }

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().update(newBuildings),
                state.getTownMapStorage().update(newMap),
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
        val mountainId = RegionId(terrainId)
        state.getRegionStorage().require(mountainId)
        HillTerrain(mountainId)
    }

    TerrainType.Mountain -> {
        val mountainId = RegionId(terrainId)
        state.getRegionStorage().require(mountainId)
        MountainTerrain(mountainId)
    }

    TerrainType.Plain -> PlainTerrain
    TerrainType.River -> {
        val riverId = RiverId(terrainId)
        state.getRiverStorage().require(riverId)
        RiverTerrain(riverId)
    }
}