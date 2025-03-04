package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.canDelete(style: ArchitecturalStyleId) = getRevivedBy(style).isEmpty() &&
        getBuildings(style).isEmpty()

fun countEachArchitecturalStyle(buildings: Collection<Building>) = buildings
    .filter { it.style != null }
    .groupingBy { it.style!! }
    .eachCount()

fun State.getRevivedBy(style: ArchitecturalStyleId) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.revival == style }

fun State.getPossibleStylesForRevival(style: ArchitecturalStyle) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.id != style.id && getDefaultCalendar().isAfterOrEqualOptional(style.start, it.start) }

fun State.getPossibleStyles(building: Building): Collection<ArchitecturalStyle> {
    if (building.constructionDate == null) {
        return getArchitecturalStyleStorage().getAll()
    }

    val calendar = getDefaultCalendar()

    return getArchitecturalStyleStorage()
        .getAll()
        .filter { calendar.isAfterOrEqualOptional(it.start, building.constructionDate) }
}
