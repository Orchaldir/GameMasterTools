package at.orchaldir.gm.core.selector.ecology.plant

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantId

fun State.canDeletePlant(plant: PlantId) = DeleteResult(plant)
