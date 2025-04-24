package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.selector.getPlanarLanguages
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getStartYear
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun State.canDeletePlane(plane: PlaneId) = getDemiplanes(plane).isEmpty()
        && getReflections(plane).isEmpty()
        && getPlanarLanguages(plane).isEmpty()
        && getMoons(plane).isEmpty()

fun State.getDemiplanes(plane: PlaneId) = getPlaneStorage()
    .getAll()
    .filter { it.purpose is Demiplane && it.purpose.plane == plane }

fun State.getHeartPlane(god: GodId) = getPlaneStorage()
    .getAll()
    .firstOrNull { it.purpose is HeartPlane && it.purpose.god == god }

fun State.getPrisonPlane(god: GodId) = getPlaneStorage()
    .getAll()
    .firstOrNull { it.purpose is PrisonPlane && it.purpose.gods.contains(god) }

fun State.getReflections(plane: PlaneId) = getPlaneStorage()
    .getAll()
    .filter { it.purpose is ReflectivePlane && it.purpose.plane == plane }

fun State.getPlanarAlignments(day: Day): Map<Plane, PlanarAlignment> {
    val results = mutableMapOf<Plane, PlanarAlignment>()

    getPlaneStorage()
        .getAll()
        .forEach { plane -> getPlanarAlignment(plane, day)?.let { results[plane] = it } }

    return results
}

fun State.getPlanarAlignment(plane: Plane, day: Day) = if (plane.purpose is IndependentPlane) {
    when (val pattern = plane.purpose.pattern) {
        is FixedAlignment -> pattern.alignment
        RandomAlignment -> null
        is PlanarCycle -> {
            val calendar = getDefaultCalendar()
            val year = calendar.getStartYear(day)
            pattern.getAlignment(year.year)
        }
    }
} else {
    null
}

fun State.getPlanarAlignments(year: Year): Map<Plane, PlanarAlignment> {
    val results = mutableMapOf<Plane, PlanarAlignment>()

    getPlaneStorage()
        .getAll()
        .forEach { plane ->
            if (plane.purpose is IndependentPlane) {
                when (val pattern = plane.purpose.pattern) {
                    is FixedAlignment -> results[plane] = pattern.alignment
                    is PlanarCycle -> results[plane] = pattern.getAlignment(year.year)
                    RandomAlignment -> doNothing()
                }
            }
        }

    return results
}

