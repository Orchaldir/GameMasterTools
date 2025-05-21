package at.orchaldir.gm.core.selector.illness

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.illness.IllnessId

fun State.canDeleteIllness(illness: IllnessId) = true

fun State.getPossibleParents(illness: IllnessId) = getIllnessStorage()
    .getAllExcept(illness)