package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.MountainId

fun State.canDelete(mountain: MountainId) = getTowns(mountain).isEmpty()

