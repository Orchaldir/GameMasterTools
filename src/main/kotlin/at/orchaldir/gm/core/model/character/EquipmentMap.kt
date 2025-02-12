package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap(val map: Map<EquipmentDataType, EquipmentId>) {

    fun contains(itemTemplate: EquipmentId) = map.containsValue(itemTemplate)
    fun contains(type: EquipmentDataType) = map.containsKey(type)

    fun getOccupiedSlots() = map.keys
        .flatMap { it.slots() }
        .toSet()
}