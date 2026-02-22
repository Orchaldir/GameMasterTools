package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.selector.util.canDeleteWithPositions
import at.orchaldir.gm.utils.Id

fun State.canDeleteRegion(region: RegionId) = DeleteResult(region)
    .addElements(getSettlementMaps(region))
    .apply { canDeleteWithPositions(region, it) }

fun State.getRegions(type: RegionDataType) = getRegionStorage()
    .getAll()
    .filter { it.data.getType() == type }

fun State.getRegions(town: SettlementMapId) = getRegionStorage().get(getRegionsIds(town))

fun State.getRegionsContaining(material: MaterialId) = getRegionStorage()
    .getAll()
    .filter { it.resources.contains(material) }

fun <ID : Id<ID>> State.getRegionsCreatedBy(id: ID) = getRegionStorage()
    .getAll()
    .filter { it.data.isCreatedBy(id) }

fun State.getRegionsIds(town: SettlementMapId) = getSettlementMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getMountain() }
    .distinct()

