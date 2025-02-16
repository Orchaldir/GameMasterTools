package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.world.plane.Demiplane
import at.orchaldir.gm.core.model.world.plane.HeartPlane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.plane.ReflectivePlane

fun State.canDeletePlane(plane: PlaneId) = true

fun State.getDemiplanes(plane: PlaneId) = getPlaneStorage()
    .getAll()
    .filter { it.purpose is Demiplane && it.purpose.plane == plane }

fun State.getHeartPlane(god: GodId) = getPlaneStorage()
    .getAll()
    .firstOrNull { it.purpose is HeartPlane && it.purpose.god == god }

fun State.getReflections(plane: PlaneId) = getPlaneStorage()
    .getAll()
    .filter { it.purpose is ReflectivePlane && it.purpose.plane == plane }


