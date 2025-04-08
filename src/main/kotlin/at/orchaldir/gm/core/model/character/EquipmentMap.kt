package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap<T>(private val map: Map<BodySlot, T>) {

    constructor() : this(emptyMap())

    fun contains(equipment: T) = map.containsValue(equipment)

    fun isFree(slots: Set<BodySlot>) = slots.none { map.containsKey(it) }

    fun getAllEquipment() = map.values.toSet()

    fun getEquipment(slots: Set<BodySlot>): T? {
        val items = slots
            .mapNotNull { map[it] }
            .toSet()

        return if (items.size == 1) {
            items.first()
        } else {
            null
        }
    }

    fun <U> convert(function: (T) -> U): EquipmentMap<U> = EquipmentMap(
        map.mapValues { function(it.value) }
    )
}