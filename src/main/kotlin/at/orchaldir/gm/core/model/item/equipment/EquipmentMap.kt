package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.render.Colors
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap<T>(private val list: List<EquipmentMapEntry<T>>) {

    constructor() : this(emptyList())
    constructor(entry: EquipmentMapEntry<T>) : this(listOf(entry))

    companion object {
        fun from(data: EquipmentData) =
            EquipmentMap(EquipmentMapEntry.from(data, data))

        fun <T> from(data: EquipmentData, second: T) =
            EquipmentMap(EquipmentMapEntry.from(Pair(data, second), data))

        fun from(list: List<EquipmentData>) =
            EquipmentMap(list.map { EquipmentMapEntry.from(it, it) })

        fun fromId(equipment: EquipmentId, scheme: ColorSchemeId, slot: BodySlot) =
            EquipmentMap(EquipmentMapEntry.fromId(equipment, scheme, slot))

        fun <T> fromSlotAsKeyMap(map: Map<BodySlot, T>) =
            EquipmentMap(map.entries.map { EquipmentMapEntry(it.value, it.key) })

        fun <T> fromSlotAsValueMap(map: Map<T, Set<Set<BodySlot>>>) =
            EquipmentMap(map.entries.toList().map { EquipmentMapEntry(it.key, it.value) })
    }

    fun size() = list.size

    fun contains(data: T) = list.any { it.data == data }

    fun getSets(data: T) = list
        .firstOrNull() { it.data == data }
        ?.sets

    fun isFree(slot: BodySlot) = list.all { it.sets.all { set -> !set.contains(slot) } }

    fun isFree(slots: Set<BodySlot>) = slots.all { isFree(it) }

    fun getAllEquipment() = list.map { it.data }
    fun getEquipmentWithSlotSets() = list

    fun getEquipment(slots: Set<BodySlot>): T? = list
        .find { it.sets.contains(slots) }
        ?.data

    fun getMaxIounStoneSlot() = list
        .map { it.getMaxIounStoneSlot() }
        .maxByOrNull { it?.getOptionalIounStoneIndex() ?: -1 }
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