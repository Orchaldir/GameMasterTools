package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.NoConstruction
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import at.orchaldir.gm.utils.update

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

val ADD_STREET_TILE: Reducer<AddStreetTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    state.getStreetStorage().require(action.street)

    require(oldTown.map.isInside(action.tileIndex)) { "Tile ${action.tileIndex} is outside the map!" }

    val tile = oldTown.map.tiles[action.tileIndex].copy(construction = StreetTile(action.street))
    val tiles = oldTown.map.tiles.update(action.tileIndex, tile)
    val town = oldTown.copy(map = oldTown.map.copy(tiles = tiles))

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

val REMOVE_STREET_TILE: Reducer<RemoveStreetTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)

    require(oldTown.map.isInside(action.tileIndex)) { "Tile ${action.tileIndex} is outside the map!" }

    val oldTile = oldTown.map.tiles[action.tileIndex]

    require(oldTile.construction is StreetTile) { "Tile ${action.tileIndex} is not a street" }

    val tile = oldTile.copy(construction = NoConstruction)
    val tiles = oldTown.map.tiles.update(action.tileIndex, tile)
    val town = oldTown.copy(map = oldTown.map.copy(tiles = tiles))

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
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