package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.item.getItemTemplatesMadeOf
import at.orchaldir.gm.core.selector.world.getStreetTemplatesMadeOf

fun State.canDelete(material: MaterialId) = getItemTemplatesMadeOf(material).isEmpty()
        && getStreetTemplatesMadeOf(material).isEmpty()

fun countMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.category }
    .eachCount()

