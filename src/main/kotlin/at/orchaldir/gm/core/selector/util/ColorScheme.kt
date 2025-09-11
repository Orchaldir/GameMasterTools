package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.item.countEquippedWith
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.item.getEquippedWith
import at.orchaldir.gm.core.selector.time.getHolidays

private val DEFAULT_COLOR_SCHEME: Colors = TwoColors.init(Color.Navy, Color.Green)

fun State.canDeleteColorScheme(id: ColorSchemeId) = DeleteResult(id)
    .addElements(getEquipment(id))
    .addElements(getEquippedWith(id))

fun State.getColors(equipment: Equipment) = getColorSchemeStorage()
    .getOptional(equipment.colorSchemes.firstOrNull())
    ?.data ?: DEFAULT_COLOR_SCHEME

fun State.getValidColorSchemes(data: EquipmentData) = getColorSchemeStorage()
    .getAll()
    .getValidColorSchemes(data)

fun Collection<ColorScheme>.getValidColorSchemes(data: EquipmentData): List<ColorScheme> {
    val requiredSchemaColors = data.requiredSchemaColors()

    return filter { it.isValid(requiredSchemaColors) }
}

fun State.filterValidColorSchemes(data: EquipmentData, ids: Set<ColorSchemeId>): Set<ColorSchemeId> {
    val requiredSchemaColors = data.requiredSchemaColors()

    return ids
        .filter { getColorSchemeStorage().getOrThrow(it).isValid(requiredSchemaColors) }
        .toSet()
}


