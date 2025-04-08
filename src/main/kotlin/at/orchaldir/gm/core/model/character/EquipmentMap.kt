package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap<T>(private val map: Map<T, Set<Set<BodySlot>>>) {

    constructor() : this(emptyMap<T, Set<Set<BodySlot>>>())
    constructor(pair: Pair<T, Set<Set<BodySlot>>>) : this(mapOf(pair))
    constructor(value: T, slot: BodySlot) : this(mapOf(value to setOf(setOf(slot))))
    constructor(value: T, slots: Set<BodySlot>) : this(mapOf(value to setOf(slots)))

    companion object {
        fun <T> fromSlotAsKeyMap(map: Map<BodySlot, T>) =
            EquipmentMap(map.entries.associate { Pair(it.value, setOf(setOf(it.key))) })
    }

    fun contains(equipment: T) = map.containsKey(equipment)

    fun isFree(slot: BodySlot) = map.values.all { sets ->
        sets.all { set -> !set.contains(slot) }
    }

    fun isFree(slots: Set<BodySlot>) = slots.all { isFree(it) }

    fun getAllEquipment() = map.keys
    fun getEquipmentWithSlotSets() = map

    fun getEquipment(slots: Set<BodySlot>): T? = map.filter { (_, slotSets) ->
        slotSets.contains(slots)
    }.firstNotNullOfOrNull { it.key }

    fun <U> convert(function: (T) -> U): EquipmentMap<U> = EquipmentMap<U>(
        map.mapKeys { function(it.key) }
    )
}