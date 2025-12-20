package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMadeOf
import at.orchaldir.gm.core.selector.item.getTextsMadeOf
import at.orchaldir.gm.core.selector.world.getMoonsContaining
import at.orchaldir.gm.core.selector.world.getRegionsContaining
import at.orchaldir.gm.core.selector.world.getStreetTemplatesMadeOf
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.math.unit.Weight

fun State.canDeleteMaterial(material: MaterialId) = DeleteResult(material)
    .addElements(getCurrencyUnits(material))
    .addElements(getEquipmentMadeOf(material))
    .addElements(getMoonsContaining(material))
    .addElements(getRegionsContaining(material))
    .addElements(getStreetTemplatesMadeOf(material))
    .addElements(getTextsMadeOf(material))

fun countEachMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.category }
    .eachCount()

fun State.calculateWeight(id: MaterialId, volume: Volume): Weight {
    val material = getMaterialStorage().getOrThrow(id)

    return Weight.fromVolume(volume, material.density)
}
