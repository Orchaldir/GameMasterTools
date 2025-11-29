package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.render.Colors
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
}

@Serializable
data class EquipmentMap<T>(private val list: List<EquipmentEntry<T>>) {

    constructor() : this(emptyList())
    constructor(entry: EquipmentEntry<T>) : this(listOf(entry))

    companion object {
        fun from(data: EquipmentData) =
            EquipmentMap(EquipmentEntry.from(data, data))

        fun <T> from(data: EquipmentData, second: T) =
            EquipmentMap(EquipmentEntry.from(Pair(data, second), data))

        fun from(list: List<EquipmentData>) =
            EquipmentMap(list.map { EquipmentEntry.from(it, it) })

        fun fromId(equipment: EquipmentId, scheme: ColorSchemeId, slot: BodySlot) =
            EquipmentMap(EquipmentEntry.fromId(equipment, scheme, slot))

        fun <T> fromSlotAsKeyMap(map: Map<BodySlot, T>) =
            EquipmentMap(map.entries.map { EquipmentEntry(it.value, it.key) })

        fun <T> fromSlotAsValueMap(map: Map<T, Set<Set<BodySlot>>>) =
            EquipmentMap(map.entries.toList().map { EquipmentEntry(it.key, it.value) })
    }

    fun size() = list.size

    fun contains(data: T) = list.any { it.data == data }

    fun isFree(slot: BodySlot) = list.all { it.sets.all { set -> !set.contains(slot) } }

    fun isFree(slots: Set<BodySlot>) = slots.all { isFree(it) }

    fun getAllEquipment() = list.map { it.data }
    fun getEquipmentWithSlotSets() = list

    fun getEquipment(slots: Set<BodySlot>): T? = list
        .find { it.sets.contains(slots) }
        ?.data
}

typealias EquipmentIdPair = Pair<EquipmentId, ColorSchemeId?>
typealias EquipmentIdMap = EquipmentMap<EquipmentIdPair>
typealias EquipmentDataPair = Pair<EquipmentData, Colors>
typealias EquipmentElementMap = EquipmentMap<EquipmentDataPair>
typealias EquipmentDataMap = EquipmentMap<EquipmentData>

fun EquipmentIdMap.containsId(equipment: EquipmentId) = getAllEquipment().any { it.first == equipment }
fun EquipmentIdMap.containsScheme(scheme: ColorSchemeId) = getAllEquipment().any { it.second == scheme }
fun EquipmentIdMap.convert(function: (EquipmentIdPair) -> EquipmentDataPair) = EquipmentElementMap(
    getEquipmentWithSlotSets().map { it.convert(function) }
)
fun EquipmentDataMap.addColors(function: (EquipmentData) -> EquipmentDataPair) = EquipmentElementMap(
    getEquipmentWithSlotSets().map { it.convert(function) }
)