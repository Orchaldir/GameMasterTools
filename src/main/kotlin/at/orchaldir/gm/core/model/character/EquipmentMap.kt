package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap(val map: Map<EquipmentDataType, ItemTemplateId>) {

    fun contains(itemTemplate: ItemTemplateId) = map.containsValue(itemTemplate)
    fun contains(type: EquipmentDataType) = map.containsKey(type)

    fun getOccupiedSlots() = map.keys
        .flatMap { it.slots() }
        .toSet()
}