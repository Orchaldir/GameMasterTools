package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.canDeleteRegion(region: RegionId) = getTowns(region).isEmpty()

fun State.getRegions(town: TownMapId) = getRegionsIds(town)
    .map { getRegionStorage().getOrThrow(it) }

fun State.getRegionsContaining(material: MaterialId) = getRegionStorage()
    .getAll()
    .filter { it.resources.contains(material) }

fun State.getRegionsIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getMountain() }
    .distinct()

