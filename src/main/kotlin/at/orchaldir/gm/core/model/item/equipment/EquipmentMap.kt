package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.render.Colors
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap<T>(private val map: Map<T, Set<Set<BodySlot>>>) {

    constructor() : this(emptyMap<T, Set<Set<BodySlot>>>())
    constructor(pair: Pair<T, Set<Set<BodySlot>>>) : this(mapOf(pair))
    constructor(value: T, slot: BodySlot) : this(mapOf(value to setOf(setOf(slot))))
    constructor(value: T, slots: Set<BodySlot>) : this(mapOf(value to setOf(slots)))

    companion object {
        fun from(data: EquipmentData) =
            EquipmentMap(data, data.slots().getAllBodySlotCombinations().first())

        fun <T> from(data: EquipmentData, second: T) =
            EquipmentMap(Pair(data, second), data.slots().getAllBodySlotCombinations().first())

        fun from(list: List<EquipmentData>) =
            EquipmentMap(list.associateWith { setOf(it.slots().getAllBodySlotCombinations().first()) })

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

    fun <U> convert(function: (T) -> U): EquipmentMap<U> = EquipmentMap(
        map.mapKeys { function(it.key) }
    )
}

typealias EquipmentIdPair = Pair<EquipmentId, ColorSchemeId>
typealias EquipmentIdMap = EquipmentMap<EquipmentIdPair>
typealias EquipmentElementMap = EquipmentMap<Pair<EquipmentData, Colors>>
typealias EquipmentDataMap = EquipmentMap<EquipmentData>