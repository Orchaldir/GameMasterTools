package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentType
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId
import io.ktor.http.*

fun parseEquipmentMap(
    parameters: Parameters,
): EquipmentMap {
    val map = mutableMapOf<EquipmentType, ItemTemplateId>()

    EquipmentType.entries.forEach { tryParse(parameters, map, it) }

    return EquipmentMap(map)
}

private fun tryParse(parameters: Parameters, map: MutableMap<EquipmentType, ItemTemplateId>, type: EquipmentType) {
    val value = parameters[type.name]

    if (!value.isNullOrBlank()) {
        map[type] = ItemTemplateId(value.toInt())
    }
}