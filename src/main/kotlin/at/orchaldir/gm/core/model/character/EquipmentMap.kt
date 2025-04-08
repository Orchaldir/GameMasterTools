package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap(val map: Map<BodySlot, EquipmentId>) {

    fun contains(equipment: EquipmentId) = map.containsValue(equipment)
    fun contains(slot: BodySlot) = map.containsKey(slot)

    fun getOccupiedSlots() = map.keys
}