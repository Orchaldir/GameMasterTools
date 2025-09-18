package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.selector.util.canDeletePosition

fun State.canDeleteWorld(world: WorldId) = DeleteResult(world)
    .apply { canDeletePosition(world, it) }



