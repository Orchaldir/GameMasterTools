package at.orchaldir.gm.core.selector.health

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isDestroyer

fun State.canDeleteDisease(disease: DiseaseId) = !isDestroyer(disease) &&
        countDiseasesBasedOn(disease) == 0

fun State.countDiseasesBasedOn(id: DiseaseId) = getDiseaseStorage()
    .getAll()
    .count { it.origin.isChildOf(id.value) }

fun State.getExistingDiseases(date: Date?) = getExistingElements(getDiseaseStorage().getAll(), date)

fun State.getDiseasesBasedOn(id: DiseaseId) = getDiseaseStorage()
    .getAll()
    .filter { it.origin.isChildOf(id.value) }
