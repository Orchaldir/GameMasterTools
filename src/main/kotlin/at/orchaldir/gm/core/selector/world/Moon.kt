package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.world.plane.PlaneId

fun State.getMoons(plane: PlaneId) = getMoonStorage()
    .getAll()
    .filter { it.plane == plane }

fun State.getMoonsContaining(material: MaterialId) = getMoonStorage()
    .getAll()
    .filter { it.resources.contains(material) }


