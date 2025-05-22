package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeId

fun State.canDeleteColorScheme(scheme: ColorSchemeId) = true

fun State.getValidColorSchemes(data: EquipmentData) = getColorSchemeStorage()
    .getAll()
    .getValidColorSchemes(data)

fun State.filterValidColorSchemes(data: EquipmentData, ids: Set<ColorSchemeId>): Set<ColorSchemeId> {
    val requiredSchemaColors = data.requiredSchemaColors()

    return ids
        .filter { getColorSchemeStorage().getOrThrow(it).isValid(requiredSchemaColors) }
        .toSet()
}

fun Collection<ColorScheme>.getValidColorSchemes(data: EquipmentData): List<ColorScheme> {
    val requiredSchemaColors = data.requiredSchemaColors()

    return filter { it.isValid(requiredSchemaColors) }
}

