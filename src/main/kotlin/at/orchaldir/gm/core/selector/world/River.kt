package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RiverId

fun State.canDelete(river: RiverId) = getTowns(river).isEmpty()

