package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.Equipment
import io.ktor.http.*

fun generateEquipment(
    config: AppearanceGeneratorConfig,
    character: Character,
): List<Equipment> {
    val type = config.generate(config.appearanceOptions.appearanceType)
    val parameters = parametersOf(APPEARANCE_TYPE, type.toString())

    return parseEquipment(parameters, config, character)
}

fun parseEquipment(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
): List<Equipment> = emptyList()