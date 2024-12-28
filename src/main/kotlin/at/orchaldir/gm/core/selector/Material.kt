package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId

fun State.canDelete(material: MaterialId) = getItemTemplatesOf(material).isEmpty()

fun State.getItemTemplatesOf(material: MaterialId) = getItemTemplateStorage().getAll()
    .filter { it.equipment.contains(material) }

fun countMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.category }
    .eachCount()

