package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.CreateTownMap
import at.orchaldir.gm.core.action.DeleteTownMap
import at.orchaldir.gm.core.action.UpdateTownMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN_MAP: Reducer<CreateTownMap, State> = { state, _ ->
    val town = TownMap(state.getTownMapStorage().nextId)

    noFollowUps(state.updateStorage(state.getTownMapStorage().add(town)))
}

val DELETE_TOWN_MAP: Reducer<DeleteTownMap, State> = { state, action ->
    state.getTownMapStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getTownMapStorage().remove(action.id)))
}

val UPDATE_TOWN_MAP: Reducer<UpdateTownMap, State> = { state, action ->
    val townMap = action.townMap
    state.getTownMapStorage().require(townMap.id)

    validateTownMap(state, townMap)

    noFollowUps(state.updateStorage(state.getTownMapStorage().update(townMap)))
}

fun validateTownMap(state: State, townMap: TownMap) {
    state.getTownStorage().requireOptional(townMap.town)
    require(!hasDuplicateTownAndDate(state, townMap)) { "Multiple maps have the same town & date combination!" }
    townMap.map.tiles.forEach { validateTownTile(state, it) }
}

private fun hasDuplicateTownAndDate(state: State, townMap: TownMap) = state
    .getTownMapStorage()
    .getAll()
    .filter { it.id != townMap.id }
    .any {
        if (it.town != null) {
            it.town == townMap.town && it.date == townMap.date
        } else {
            false
        }
    }

private fun validateTownTile(state: State, tile: TownTile) {
    validateConstruction(state, tile.construction)
    validateTerrain(state, tile.terrain)
}

private fun validateConstruction(state: State, construction: Construction) = when (construction) {
    AbstractBuildingTile -> doNothing()
    is AbstractLargeBuildingStart -> doNothing()
    AbstractLargeBuildingTile -> doNothing()
    is BuildingTile -> state.getBuildingStorage().require(construction.building)
    NoConstruction -> doNothing()
    is StreetTile -> {
        state.getStreetTemplateStorage().require(construction.templateId)
        state.getStreetStorage().requireOptional(construction.streetId)
    }
}

private fun validateTerrain(state: State, terrain: Terrain) = when (terrain) {
    is HillTerrain -> validateRegion(state, terrain.mountain)
    is MountainTerrain -> validateRegion(state, terrain.mountain)
    PlainTerrain -> doNothing()
    is RiverTerrain -> state.getRiverStorage().require(terrain.river)
}

private fun validateRegion(
    state: State,
    id: RegionId,
) {
    val region = state.getRegionStorage().getOrThrow(id)

    require(region.data.getType() == RegionDataType.Mountain) { "${id.type()} ${id.value} must be a mountain!" }
}
