package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.canDelete(mountain: MountainId) = getTowns(mountain).isEmpty()

fun State.getMountains(town: TownMapId) = getMountainIds(town)
    .map { getMountainStorage().getOrThrow(it) }

fun State.getMountainsContaining(material: MaterialId) = getMountainStorage()
    .getAll()
    .filter { it.resources.contains(material) }

fun State.getMountainIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getMountain() }
    .distinct()

