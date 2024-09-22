package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.canDelete(river: RiverId) = getTowns(river).isEmpty()

fun State.getRivers(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getRiver() }.distinct()


