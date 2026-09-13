package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearanceType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.SomeOf
import kotlinx.serialization.Serializable

private val EMPTY = OneOrNone<EquipmentId>()

@Serializable
data class ClothingFashion(
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.Naked),
    val accessories: SomeOf<EquipmentAppearanceType> = SomeOf(emptySet()),
    val equipmentRarityMap: Map<EquipmentAppearanceType, OneOrNone<EquipmentId>> = emptyMap(),
) {

    fun getAllEquipment() = equipmentRarityMap
        .values
        .flatMap { it.getValidValues() }

    fun getOptions(type: EquipmentAppearanceType) = equipmentRarityMap[type] ?: EMPTY

}