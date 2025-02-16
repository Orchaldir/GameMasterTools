package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.PlaneId

fun State.getMoons(plane: PlaneId) = getMoonStorage()
    .getAll()
    .filter { it.plane == plane }


