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

    fun isFree(slots: Set<BodySlot>) = slots.none { map.containsKey(it) }

    fun getEquipment(slots: Set<BodySlot>): EquipmentId? {
        val items = slots
            .mapNotNull { map[it] }
            .toSet()

        return if (items.size == 1) {
            items.first()
        } else {
            null
        }
    }
}