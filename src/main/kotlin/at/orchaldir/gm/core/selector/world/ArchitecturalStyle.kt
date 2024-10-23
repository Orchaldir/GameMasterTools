package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.canDelete(style: ArchitecturalStyleId) = getRevivedBy(style).isEmpty() &&
        getBuildings(style).isEmpty()

fun State.getArchitecturalStyles(town: TownId) = countArchitecturalStyles(getBuildings(town))

fun countArchitecturalStyles(buildings: Collection<Building>) = buildings
    .groupingBy { it.architecturalStyle }
    .eachCount()

fun State.getRevivedBy(style: ArchitecturalStyleId) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.revival == style }

fun State.getPossibleStylesForRevival(style: ArchitecturalStyle) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.id != style.id && it.start < style.start }

fun State.getPossibleStyles(building: Building): List<ArchitecturalStyle> {
    val year = getDefaultCalendar().getYear(building.constructionDate)

    return getArchitecturalStyleStorage()
        .getAll()
        .filter { it.start < year }
}
