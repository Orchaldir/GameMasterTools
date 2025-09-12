package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.selector.util.canDeleteWithPositions

fun State.canDeleteWorld(world: WorldId) = DeleteResult(world)



