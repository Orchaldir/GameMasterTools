package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.selector.util.canDeleteTargetOfBelief

fun State.canDeletePantheon(pantheon: PantheonId) = DeleteResult(pantheon)
    .apply { canDeleteTargetOfBelief(pantheon, it) }

fun State.getPantheonsContaining(god: GodId) = getPantheonStorage()
    .getAll()
    .filter { it.gods.contains(god) }
