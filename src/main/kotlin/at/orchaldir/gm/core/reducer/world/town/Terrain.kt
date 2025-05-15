package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.ResizeTown
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val SET_TERRAIN_TILE: Reducer<SetTerrainTile, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val terrain = createTerrain(state, action.terrainType, action.terrainId)
    val map = oldMap.setTerrain(action.tileIndex, terrain)

    noFollowUps(state.updateStorage(state.getTownStorage().update(map)))
}

val RESIZE_TERRAIN: Reducer<ResizeTown, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val terrain = createTerrain(state, action.terrainType, action.terrainId)
    val tile = TownTile(terrain)

    val newTileMap = oldMap.map.resize(action.resize, tile)
    val newMap = oldMap.copy(map = newTileMap)
    val newBuildings = state.getBuildings(action.town)
        .map { building ->
            val newIndex = oldMap.map.getIndexAfterResize(building.lot.tileIndex, action.resize)

            if (newIndex != null) {
                building.copy(lot = building.lot.copy(tileIndex = newIndex))
            } else {
                error("Resize would remove building ${building.id.value}!")
            }
        }

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().update(newBuildings),
                state.getTownStorage().update(newMap),
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