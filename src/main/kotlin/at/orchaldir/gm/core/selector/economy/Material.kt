package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialCategoryType
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
    .addElements(getMaterialsMadeOf(material))
    .addElements(getMoonsContaining(material))
    .addElements(getRegionsContaining(material))
    .addElements(getStreetTemplatesMadeOf(material))
    .addElements(getTextsMadeOf(material))

fun countEachMaterialCategory(materials: Collection<Material>) = materials
    .groupingBy { it.properties.category.getType() }
    .eachCount()

fun State.calculateWeight(id: MaterialId, volume: Volume): Weight {
    val material = getMaterialStorage().getOrThrow(id)

    return Weight.fromVolume(volume, material.properties.density)
}

fun State.getFirstMaterial(category: MaterialCategoryType) = getMaterialStorage()
    .getAll()
    .first() { it.properties.category.getType() == category }

fun State.getFirstMaterial(categories: Set<MaterialCategoryType>) = getMaterialStorage()
    .getAll()
    .first() { categories.contains(it.properties.category.getType()) }

fun State.getMaterials(categories: Set<MaterialCategoryType>) = getMaterialStorage()
    .getAll()
    .filter { categories.contains(it.properties.category.getType()) }

fun State.getMaterialIds(categories: Set<MaterialCategoryType>) = getMaterials(categories)
    .map { it.id }

fun State.getMaterialsMadeOf(other: MaterialId) = getMaterialStorage()
    .getAll()
    .filter { it.properties.contains(other) }

fun State.getMaterialColor(id: MaterialId) = getMaterialStorage().getOrThrow(id).properties.color
