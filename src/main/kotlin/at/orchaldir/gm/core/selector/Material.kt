package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.world.getStreetTypesMadeOf

fun State.canDelete(material: MaterialId) = getItemTemplatesMadeOf(material).isEmpty()
        && getStreetTypesMadeOf(material).isEmpty()

fun countMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.category }
    .eachCount()

