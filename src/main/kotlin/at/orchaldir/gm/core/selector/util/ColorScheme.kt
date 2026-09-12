package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearance
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.item.equipment.getEquipment
import at.orchaldir.gm.core.selector.item.equipment.getEquippedWith

fun State.canDeleteColorScheme(id: ColorSchemeId) = DeleteResult(id)
    .addElements(getColorSchemeGroups(id))
    .addElements(getEquipment(id))
    .addElements(getEquippedWith(id))

fun State.getValidColorSchemes(data: EquipmentAppearance) = getColorSchemeStorage()
    .getAll()
    .getValidColorSchemes(data)

fun Collection<ColorScheme>.getValidColorSchemes(data: EquipmentAppearance): List<ColorScheme> {
    val requiredSchemaColors = data.requiredSchemaColors()

    return filter { it.isValid(requiredSchemaColors) }
}

fun State.filterValidColorSchemes(data: EquipmentAppearance, ids: Set<ColorSchemeId>): Set<ColorSchemeId> {
    val requiredSchemaColors = data.requiredSchemaColors()

    return ids
        .filter { getColorSchemeStorage().getOrThrow(it).isValid(requiredSchemaColors) }
        .toSet()
}


