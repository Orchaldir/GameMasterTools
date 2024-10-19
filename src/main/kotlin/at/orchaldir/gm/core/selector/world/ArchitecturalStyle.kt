package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId

fun State.getRevivedBy(style: ArchitecturalStyleId) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.revival == style }

fun State.getPossibleStylesForRevival(style: ArchitecturalStyle) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.id != style.id && it.start < style.start }

