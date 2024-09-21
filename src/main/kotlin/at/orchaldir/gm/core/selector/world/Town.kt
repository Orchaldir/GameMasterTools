package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId

fun State.getTowns(mountain: MountainId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(mountain) } }

fun State.getTowns(river: RiverId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(river) } }

fun State.getTowns(street: StreetId) = getTownStorage().getAll()
    .filter { it.map.contains { it.construction.contains(street) } }

