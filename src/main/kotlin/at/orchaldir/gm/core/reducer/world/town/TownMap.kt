package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.utils.doNothing

fun hasDuplicateTownAndDate(state: State, townMap: TownMap) = state
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

fun validateTownTile(state: State, tile: TownTile) {
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
