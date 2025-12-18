package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentEntry<T>(val data: T, val sets: Set<Set<BodySlot>>) {
    constructor(value: T, slot: BodySlot) : this(value, setOf(setOf(slot)))

    companion object {
        fun <T> from(value: T, slots: Set<BodySlot>) = EquipmentEntry(value, setOf(slots))
        fun <T> from(value: T, data: EquipmentData) = from(value, data.slots().getAllBodySlotCombinations().first())
        fun fromId(equipment: EquipmentId, scheme: ColorSchemeId, slot: BodySlot) =
            EquipmentEntry(EquipmentIdPair(equipment, scheme), slot)
    }

    fun <U> convert(function: (T) -> U): EquipmentEntry<U> = EquipmentEntry(
        function(data),
        sets,
    )

    fun getMaxIounStoneSlot(): BodySlot? {
        var maxIndex = -1
        var maxSlot: BodySlot? = null

        sets.forEach { slots ->
            slots.forEach { slot ->
                val index = slot.getOptionalIounStoneIndex() ?: return@forEach

                if (index > maxIndex) {
                    maxIndex = index
                    maxSlot = slot
                }
            }
        }

        return maxSlot
    }
}
