package at.orchaldir.gm.core.selector.illness

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.hasChildren

fun State.canDeleteIllness(illness: IllnessId) = !hasChildren(illness)

fun State.getPossibleParents(illness: IllnessId) = getIllnessStorage()
    .getAllExcept(illness)

fun State.getExistingIllnesses(date: Date?) = getExistingElements(getIllnessStorage().getAll(), date)