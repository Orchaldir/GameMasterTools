package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap<T>(private val map: Map<T, Set<Set<BodySlot>>>) {

    constructor() : this(emptyMap())

    fun contains(equipment: T) = map.containsKey(equipment)

    fun isFree(slot: BodySlot) = map.values.all { sets ->
        sets.all { set -> !set.contains(slot) }
    }

    fun isFree(slots: Set<BodySlot>) = slots.all { isFree(it) }

    fun getAllEquipment() = map.keys

    fun getEquipment(slots: Set<BodySlot>): T? = map.filter { (_, slotSets) ->
        slotSets.contains(slots)
    }.firstNotNullOfOrNull { it.key }

    fun <U> convert(function: (T) -> U): EquipmentMap<U> = EquipmentMap(
        map.mapKeys { function(it.key) }
    )
}