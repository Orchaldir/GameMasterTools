package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId

fun State.canDeletePantheon(pantheon: PantheonId) = true

fun State.getPantheonsContaining(god: GodId) = getPantheonStorage()
    .getAll()
    .filter { it.gods.contains(god) }
