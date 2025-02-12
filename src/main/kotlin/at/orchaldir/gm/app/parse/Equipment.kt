package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import io.ktor.http.*

fun parseEquipmentMap(
    parameters: Parameters,
): EquipmentMap {
    val map = mutableMapOf<EquipmentDataType, EquipmentId>()

    EquipmentDataType.entries.forEach { tryParse(parameters, map, it) }

    return EquipmentMap(map)
}

private fun tryParse(
    parameters: Parameters,
    map: MutableMap<EquipmentDataType, EquipmentId>,
    type: EquipmentDataType,
) {
    val value = parameters[type.name]

    if (!value.isNullOrBlank()) {
        map[type] = EquipmentId(value.toInt())
    }
}