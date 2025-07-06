package at.orchaldir.gm.core.selector.health

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.DiseaseId

fun State.canDeleteDisease(spell: DiseaseId) = false
