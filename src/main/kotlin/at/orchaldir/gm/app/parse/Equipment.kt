package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId
import io.ktor.http.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun generateEquipment(
    config: EquipmentGenerator,
    character: Character,
): EquipmentMap = EquipmentMap(emptyMap())

fun parseEquipmentMap(
    parameters: Parameters,
): EquipmentMap {
    val map = mutableMapOf<EquipmentType, ItemTemplateId>()

    logger.info { "params: $parameters" }

    EquipmentType.entries.forEach { tryParse(parameters, map, it) }

    logger.info { "map: $map" }

    return EquipmentMap(map)
}

private fun tryParse(parameters: Parameters, map: MutableMap<EquipmentType, ItemTemplateId>, type: EquipmentType) {
    val value = parameters[type.name]

    if (!value.isNullOrBlank()) {
        map[type] = ItemTemplateId(value.toInt())
    }
}