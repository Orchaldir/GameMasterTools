package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.SomeOf
import kotlinx.serialization.Serializable

private val EMPTY = OneOrNone<EquipmentId>()

@Serializable
data class ClothingFashion(
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.Naked),
    val accessories: SomeOf<EquipmentDataType> = SomeOf(emptySet()),
    val equipmentRarityMap: Map<EquipmentDataType, OneOrNone<EquipmentId>> = emptyMap(),
) {

    fun getAllEquipment() = equipmentRarityMap
        .values
        .flatMap { it.getValidValues() }

    fun getOptions(type: EquipmentDataType) = equipmentRarityMap[type] ?: EMPTY

}