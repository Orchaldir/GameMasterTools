package at.orchaldir.gm.core.reducer.world.settlement

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.settlement.*
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.utils.doNothing

fun hasDuplicateSettlementAndDate(state: State, map: SettlementMap) = state
    .getSettlementMapStorage()
    .getAll()
    .filter { it.id != map.id }
    .any {
        if (it.settlement != null) {
            it.settlement == map.settlement && it.date == map.date
        } else {
            false
        }
    }

fun validateSettlementTile(state: State, tile: SettlementTile) {
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
