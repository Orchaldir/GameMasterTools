package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.region.RiverId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.canDelete(river: RiverId) = getTowns(river).isEmpty()

fun State.getRivers(town: TownMapId) = getRiverIds(town)
    .map { getRiverStorage().getOrThrow(it) }

fun State.getRiverIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getRiver() }
    .distinct()


