package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.world.countStreetTemplates

fun State.canDeleteMaterial(material: MaterialId) = countCurrencyUnits(material) == 0
        && countEquipment(material) == 0
        && countStreetTemplates(material) == 0
        && countTexts(material) == 0

fun countEachMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.category }
    .eachCount()

