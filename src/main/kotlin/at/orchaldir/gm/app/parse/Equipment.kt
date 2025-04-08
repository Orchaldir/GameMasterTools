package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import io.ktor.http.*

fun parseEquipmentMap(
    parameters: Parameters,
): EquipmentMap {
    val map = mutableMapOf<BodySlot, EquipmentId>()

    BodySlot.entries.forEach { tryParse(parameters, map, it) }

    return EquipmentMap(map)
}

private fun tryParse(
    parameters: Parameters,
    map: MutableMap<BodySlot, EquipmentId>,
    slot: BodySlot,
) {
    val value = parameters[slot.name]

    if (!value.isNullOrBlank()) {
        map[slot] = EquipmentId(value.toInt())
    }
}