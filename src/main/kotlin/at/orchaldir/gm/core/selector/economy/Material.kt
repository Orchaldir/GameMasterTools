package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.world.countStreetTemplates
import at.orchaldir.gm.core.selector.world.getMoonsContaining
import at.orchaldir.gm.core.selector.world.getRegionsContaining
import at.orchaldir.gm.utils.math.unit.Weight

fun State.canDeleteMaterial(material: MaterialId) = countCurrencyUnits(material) == 0
        && countEquipment(material) == 0
        && countStreetTemplates(material) == 0
        && countTexts(material) == 0
        && getMoonsContaining(material).isEmpty()
        && getRegionsContaining(material).isEmpty()

fun countEachMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.category }
    .eachCount()

fun State.calculateWeight(id: MaterialId, volume: Float): Weight {
    val material = getMaterialStorage().getOrThrow(id)

    return Weight.fromVolume(volume, material.density)
}
