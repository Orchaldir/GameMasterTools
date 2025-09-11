package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.selector.util.exists
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteArchitecturalStyle(style: ArchitecturalStyleId) = DeleteResult(style)
    .addElements(getRevivedBy(style))
    .addElements(getBuildings(style))

fun countEachArchitecturalStyle(buildings: Collection<Building>) = buildings
    .filter { it.style != null }
    .groupingBy { it.style!! }
    .eachCount()

fun State.getRevivedBy(style: ArchitecturalStyleId) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.revival == style }

fun State.getPossibleStylesForRevival(style: ArchitecturalStyle) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.id != style.id && exists(style, it.start) }

fun State.getPossibleStyles(building: Building) =
    getExistingElements(getArchitecturalStyleStorage().getAll(), building.constructionDate)
