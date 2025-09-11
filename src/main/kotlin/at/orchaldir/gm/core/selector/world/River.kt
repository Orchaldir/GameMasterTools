package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.canDeleteRiver(river: RiverId) = DeleteResult(river)
    .addElements(getTowns(river))

fun State.getRivers(town: TownMapId) = getRiverIds(town)
    .map { getRiverStorage().getOrThrow(it) }

fun State.getRiverIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getRiver() }
    .distinct()


