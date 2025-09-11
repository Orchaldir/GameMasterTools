package at.orchaldir.gm.core.selector.health

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.canDeleteDestroyer
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteDisease(disease: DiseaseId) = DeleteResult(disease)
    .addElements(getDiseasesBasedOn(disease))
    .apply { canDeleteDestroyer(disease, it) }

fun State.getExistingDiseases(date: Date?) = getExistingElements(getDiseaseStorage().getAll(), date)

fun State.getDiseasesBasedOn(id: DiseaseId) = getDiseaseStorage()
    .getAll()
    .filter { it.origin.isChildOf(id.value) }
