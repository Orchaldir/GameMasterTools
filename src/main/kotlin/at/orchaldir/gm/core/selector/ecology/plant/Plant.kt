package at.orchaldir.gm.core.selector.ecology.plant

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantId
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.selector.ecology.canDeleteEcologyParticipant

fun State.canDeletePlant(plant: PlantId) = DeleteResult(plant)
    .apply { canDeleteEcologyParticipant(plant, it) }

fun State.getPlantsMadeOf(other: MaterialId) = getPlantStorage()
    .getAll()
    .filter { it.appearance.contains(other) }
