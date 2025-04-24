package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.util.checkComplexName
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN: Reducer<CreateTown, State> = { state, _ ->
    val town = Town(state.getTownStorage().nextId, foundingDate = state.time.currentDate)

    noFollowUps(state.updateStorage(state.getTownStorage().add(town)))
}

val DELETE_TOWN: Reducer<DeleteTown, State> = { state, action ->
    state.getTownStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)

    noFollowUps(state.updateStorage(state.getTownStorage().remove(action.id)))
}

val UPDATE_TOWN: Reducer<UpdateTown, State> = { state, action ->
    val town = action.town
    state.getTownStorage().require(town.id)

    checkComplexName(state, town.name)
    checkDate(state, town.foundingDate, "Town")
    validateCreator(state, town.founder, town.id, town.foundingDate, "founder")

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

// town's streets

val ADD_STREET_TILE: Reducer<AddStreetTile, State> = { state, action ->
    state.getStreetTemplateStorage().require(action.type)

    action.street?.let { state.getStreetStorage().require(it) }

    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.build(action.tileIndex, StreetTile(action.type, action.street))

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
    val newBuildings = state.getBuildings(action.town)
        .map { building ->
            val newIndex = oldTown.map.getIndexAfterResize(building.lot.tileIndex, action.resize)

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