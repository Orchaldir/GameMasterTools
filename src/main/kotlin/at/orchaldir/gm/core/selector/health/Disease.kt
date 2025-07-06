package at.orchaldir.gm.core.selector.health

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteDisease(spell: DiseaseId) = false

fun State.getExistingDiseases(date: Date?) = getExistingElements(getDiseaseStorage().getAll(), date)
