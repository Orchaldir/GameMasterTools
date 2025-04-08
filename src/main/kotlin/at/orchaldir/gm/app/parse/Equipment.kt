package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import io.ktor.http.*

fun parseEquipmentMap(
    parameters: Parameters,
): EquipmentMap<EquipmentId> {
    val map = mutableMapOf<EquipmentId, MutableSet<Set<BodySlot>>>()

    parameters.forEach { slotStrings, ids ->
        tryParse(map, slotStrings, ids)
    }

    return EquipmentMap(map)
}

private fun tryParse(
    map: MutableMap<EquipmentId, MutableSet<Set<BodySlot>>>,
    slotStrings: String,
    ids: List<String>,
) {
    val filteredIds = ids.filter { it.isNotEmpty() }
    require(filteredIds.size <= 1) { "Slots $slotStrings has too many items!" }
    val id = EquipmentId(filteredIds.firstOrNull()?.toInt() ?: return)

    val slots = slotStrings.split("_")
        .map { BodySlot.valueOf(it) }
        .toSet()

    map.computeIfAbsent(id) { mutableSetOf() }
        .add(slots)
}