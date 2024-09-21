package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.canDelete(mountain: MountainId) = getTowns(mountain).isEmpty()

fun State.getMountains(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getMountain() }.distinct()

