package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RiverId

fun State.getTowns(river: RiverId) = getTownStorage().getAll()
    .filter { it.map.contains { it.contains(river) } }

