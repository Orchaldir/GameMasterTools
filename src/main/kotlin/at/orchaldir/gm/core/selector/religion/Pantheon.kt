package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.selector.character.getBelievers

fun State.canDeletePantheon(pantheon: PantheonId) = getBelievers(pantheon).isEmpty()

fun State.getPantheonsContaining(god: GodId) = getPantheonStorage()
    .getAll()
    .filter { it.gods.contains(god) }
