package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.getMountains(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getMountain() }.distinct()

fun State.getRivers(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getRiver() }.distinct()

fun State.getStreets(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.construction.getStreet() }.distinct()

fun State.getTowns(mountain: MountainId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(mountain) } }

fun State.getTowns(river: RiverId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(river) } }

