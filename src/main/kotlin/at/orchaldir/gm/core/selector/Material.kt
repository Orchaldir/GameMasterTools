package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId

fun State.canDelete(material: MaterialId) = getItemTemplates(material).isEmpty()

fun State.getItemTemplates(material: MaterialId) = itemTemplates.getAll()
    .filter { it.equipment.contains(material) }

