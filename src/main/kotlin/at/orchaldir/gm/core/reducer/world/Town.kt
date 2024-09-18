package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTerrain
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
    noFollowUps(state.updateStorage(state.getTownStorage().update(action.town)))
}

val UPDATE_TERRAIN: Reducer<UpdateTerrain, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val terrain = when (action.terrainType) {
        TerrainType.Hill -> HillTerrain(MountainId(action.terrainId))
        TerrainType.Mountain -> MountainTerrain(MountainId(action.terrainId))
        TerrainType.Plain -> PlainTerrain
        TerrainType.River -> RiverTerrain(RiverId(action.terrainId))
    }
    val tile = oldTown.map.tiles[action.tileIndex].copy(terrain = terrain)
    val tiles = oldTown.map.tiles.update(action.tileIndex, tile)
    val town = oldTown.copy(map = oldTown.map.copy(tiles = tiles))

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}