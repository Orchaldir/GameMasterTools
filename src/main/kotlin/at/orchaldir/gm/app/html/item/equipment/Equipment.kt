package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.color.parseColorSchemeId
import at.orchaldir.gm.app.html.util.fieldWeight
import at.orchaldir.gm.app.html.util.parseWeight
import at.orchaldir.gm.app.html.util.selectWeight
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.util.filterValidColorSchemes
import at.orchaldir.gm.core.selector.util.getValidColorSchemes
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipment(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    fieldWeight("Weight", equipment.weight)
    fieldIds(call, state, "Color Schemes", equipment.colorSchemes)
    showEquipmentData(call, state, equipment)
}

private fun HtmlBlockTag.showEquipmentData(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    field("Type", equipment.data.getType())

    when (val data = equipment.data) {
        is OneHandedAxe -> showOneHandedAxe(call, state, data)
        is TwoHandedAxe -> showTwoHandedAxe(call, state, data)
        is Belt -> showBelt(call, state, data)
        is BodyArmour -> showBodyArmour(call, state, data)
        is Coat -> showCoat(call, state, data)
        is Dress -> showDress(call, state, data)
        is Earring -> showEarring(call, state, data)
        is EyePatch -> showEyePatch(call, state, data)
        is Footwear -> showFootwear(call, state, data)
        is Glasses -> showGlasses(call, state, data)
        is Gloves -> showGloves(call, state, data)
        is Hat -> showHat(call, state, data)
        is Helmet -> showHelmet(call, state, data)
        is Necklace -> showNecklace(call, state, data)
        is Pants -> showPants(call, state, data)
        is Polearm -> showPolearm(call, state, data)
        is Shield -> showShield(call, state, data)
        is Shirt -> showShirt(call, state, data)
        is Skirt -> showSkirt(call, state, data)
        is Socks -> showSocks(call, state, data)
        is SuitJacket -> showSuitJacket(call, state, data)
        is OneHandedSword -> showOneHandedSword(call, state, data)
        is TwoHandedSword -> showTwoHandedSword(call, state, data)
        is Tie -> showTie(call, state, data)
    }
}

// edit

fun FORM.editEquipment(
    state: State,
    equipment: Equipment,
) {
    selectName(equipment.name)
    selectWeight("Weight", WEIGHT, equipment.weight, MIN_EQUIPMENT_WEIGHT, 10000, SiPrefix.Base)
    selectColorSchemes(state, equipment)
    selectValue(
        "Equipment",
        combine(EQUIPMENT, TYPE),
        EquipmentDataType.entries,
        equipment.data.getType(),
    )
    editEquipmentData(state, equipment)
}

private fun FORM.selectColorSchemes(
    state: State,
    equipment: Equipment,
) {
    val requiredSchemaColors = equipment.data.requiredSchemaColors()

    if (requiredSchemaColors > 0) {
        val colorSchemes = state.getValidColorSchemes(equipment.data)

        field("Required Schema Colors", requiredSchemaColors)
        selectElements(
            state,
            "Color Schemas",
            combine(COLOR, SCHEME),
            colorSchemes,
            equipment.colorSchemes,
        )
    }
}

private fun FORM.editEquipmentData(
    state: State,
    equipment: Equipment,
) {
    when (val data = equipment.data) {
        is OneHandedAxe -> editOneHandedAxe(state, data)
        is TwoHandedAxe -> editTwoHandedAxe(state, data)
        is Belt -> editBelt(state, data)
        is BodyArmour -> editBodyArmour(state, data)
        is Coat -> editCoat(state, data)
        is Dress -> editDress(state, data)
        is Earring -> editEarring(state, data)
        is EyePatch -> editEyePatch(state, data)
        is Footwear -> editFootwear(state, data)
        is Glasses -> editGlasses(state, data)
        is Gloves -> editGloves(state, data)
        is Hat -> editHat(state, data)
        is Helmet -> editHelmet(state, data)
        is Necklace -> editNecklace(state, data)
        is Pants -> editPants(state, data)
        is Polearm -> editPolearm(state, data)
        is Shield -> editShield(state, data)
        is Shirt -> editShirt(state, data)
        is Skirt -> editSkirt(state, data)
        is Socks -> editSocks(state, data)
        is SuitJacket -> editSuitJacket(state, data)
        is OneHandedSword -> editOneHandedSword(state, data)
        is TwoHandedSword -> editTwoHandedSword(state, data)
        is Tie -> editTie(state, data)
    }
}

// parse

fun parseEquipmentId(value: String) = EquipmentId(value.toInt())

fun parseEquipmentId(parameters: Parameters, param: String) = EquipmentId(parseInt(parameters, param))

fun parseEquipment(
    state: State,
    parameters: Parameters,
    id: EquipmentId,
): Equipment {
    val data = parseEquipmentData(parameters)

    return Equipment(
        id,
        parseName(parameters),
        data,
        parseWeight(parameters, WEIGHT, SiPrefix.Base),
        parseColorSchemes(state, parameters, data),
    )
}

private fun parseColorSchemes(
    state: State,
    parameters: Parameters,
    data: EquipmentData,
): Set<ColorSchemeId> {
    val colorSchemeIds = parseElements(
        parameters,
        combine(COLOR, SCHEME),
        ::parseColorSchemeId,
    )

    return state.filterValidColorSchemes(data, colorSchemeIds)
}

fun parseEquipmentData(parameters: Parameters) =
    when (parse(parameters, combine(EQUIPMENT, TYPE), EquipmentDataType.Belt)) {
        EquipmentDataType.OneHandedAxe -> parseOneHandedAxe(parameters)
        EquipmentDataType.TwoHandedAxe -> parseTwoHandedAxe(parameters)
        EquipmentDataType.Belt -> parseBelt(parameters)
        EquipmentDataType.BodyArmour -> parseBodyArmour(parameters)
        EquipmentDataType.Coat -> parseCoat(parameters)
        EquipmentDataType.Dress -> parseDress(parameters)
        EquipmentDataType.Earring -> parseEarring(parameters)
        EquipmentDataType.EyePatch -> parseEyePatch(parameters)
        EquipmentDataType.Footwear -> parseFootwear(parameters)
        EquipmentDataType.Glasses -> parseGlasses(parameters)
        EquipmentDataType.Gloves -> parseGloves(parameters)
        EquipmentDataType.Hat -> parseHat(parameters)
        EquipmentDataType.Helmet -> parseHelmet(parameters)
        EquipmentDataType.Necklace -> parseNecklace(parameters)
        EquipmentDataType.Pants -> parsePants(parameters)
        EquipmentDataType.Polearm -> parsePolearm(parameters)
        EquipmentDataType.Shield -> parseShield(parameters)
        EquipmentDataType.Shirt -> parseShirt(parameters)
        EquipmentDataType.Skirt -> parseSkirt(parameters)
        EquipmentDataType.Socks -> parseSocks(parameters)
        EquipmentDataType.SuitJacket -> parseSuitJacket(parameters)
        EquipmentDataType.OneHandedSword -> parseOneHandedSword(parameters)
        EquipmentDataType.TwoHandedSword -> parseTwoHandedSword(parameters)
        EquipmentDataType.Tie -> parseTie(parameters)
    }
