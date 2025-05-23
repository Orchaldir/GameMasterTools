package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.editFillLookupItemPart
import at.orchaldir.gm.app.html.item.parseFillLookupItemPart
import at.orchaldir.gm.app.html.item.showFillLookupItemPart
import at.orchaldir.gm.app.html.util.color.parseColorSchemeId
import at.orchaldir.gm.app.html.util.fieldWeight
import at.orchaldir.gm.app.html.util.parseWeight
import at.orchaldir.gm.app.html.util.selectWeight
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.util.filterValidColorSchemes
import at.orchaldir.gm.core.selector.util.getValidColorSchemes
import at.orchaldir.gm.core.selector.util.sortColorSchemes
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
    fieldIdList(call, state, "Color Schemes", equipment.colorSchemes)
    showEquipmentData(call, state, equipment)
}

private fun HtmlBlockTag.showEquipmentData(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    field("Type", equipment.data.getType())

    when (val data = equipment.data) {
        is Belt -> showBelt(call, state, data)
        is Coat -> showCoat(call, state, data)
        is Dress -> showDress(call, state, data)
        is Earring -> showEarring(call, state, data)
        is EyePatch -> showEyePatch(call, state, data)
        is Footwear -> showFootwear(call, state, data)
        is Glasses -> showGlasses(call, state, data)
        is Gloves -> showGloves(call, state, data)
        is Hat -> showHat(call, state, data)
        is Necklace -> showNecklace(call, state, data)
        is Pants -> showPants(call, state, data)
        is Shirt -> showShirt(call, state, data)

        is Skirt -> {
            field("Style", data.style)
            showFillLookupItemPart(call, state, data.main, "Main")
        }

        is Socks -> {
            field("Style", data.style)
            showFillLookupItemPart(call, state, data.main, "Main")
        }

        is SuitJacket -> showSuitJacket(call, state, data)

        is Tie -> {
            field("Style", data.style)
            field("Size", data.size)
            showFillLookupItemPart(call, state, data.main, "Main")
            showFillLookupItemPart(call, state, data.knot, "Knot")
        }
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
            state.sortColorSchemes(colorSchemes),
            equipment.colorSchemes,
        )
    }
}

private fun FORM.editEquipmentData(
    state: State,
    equipment: Equipment,
) {
    when (val data = equipment.data) {
        is Belt -> editBelt(state, data)
        is Coat -> editCoat(state, data)
        is Dress -> editDress(state, data)
        is Earring -> editEarring(state, data)
        is EyePatch -> editEyePatch(state, data)
        is Footwear -> editFootwear(state, data)
        is Glasses -> editGlasses(state, data)
        is Gloves -> editGloves(state, data)
        is Hat -> editHat(state, data)
        is Necklace -> editNecklace(state, data)
        is Pants -> editPants(state, data)
        is Shirt -> editShirt(state, data)

        is Skirt -> {
            selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, data.style)
            editFillLookupItemPart(state, data.main, MAIN, "Main")
        }

        is Socks -> {
            selectValue("Style", STYLE, SocksStyle.entries, data.style)
            editFillLookupItemPart(state, data.main, MAIN, "Main")
        }

        is SuitJacket -> editSuitJacket(state, data)

        is Tie -> {
            selectValue("Style", STYLE, TieStyle.entries, data.style)
            selectValue("Size", SIZE, Size.entries, data.size)
            editFillLookupItemPart(state, data.main, MAIN, "Main")
            editFillLookupItemPart(state, data.knot, KNOT, "Knot")
        }
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
        EquipmentDataType.Belt -> parseBelt(parameters)
        EquipmentDataType.Coat -> parseCoat(parameters)
        EquipmentDataType.Dress -> parseDress(parameters)
        EquipmentDataType.Earring -> parseEarring(parameters)
        EquipmentDataType.EyePatch -> parseEyePatch(parameters)
        EquipmentDataType.Footwear -> parseFootwear(parameters)
        EquipmentDataType.Glasses -> parseGlasses(parameters)
        EquipmentDataType.Gloves -> parseGloves(parameters)
        EquipmentDataType.Hat -> parseHat(parameters)
        EquipmentDataType.Necklace -> parseNecklace(parameters)
        EquipmentDataType.Pants -> parsePants(parameters)
        EquipmentDataType.Shirt -> parseShirt(parameters)

        EquipmentDataType.Skirt -> Skirt(
            parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
            parseFillLookupItemPart(parameters, MAIN),
        )

        EquipmentDataType.Socks -> Socks(
            parse(parameters, STYLE, SocksStyle.Quarter),
            parseFillLookupItemPart(parameters, MAIN),
        )

        EquipmentDataType.SuitJacket -> parseSuitJacket(parameters)

        EquipmentDataType.Tie -> parseTie(parameters)
    }

private fun parseTie(parameters: Parameters) = Tie(
    parse(parameters, STYLE, TieStyle.Tie),
    parse(parameters, SIZE, Size.Medium),
    parseFillLookupItemPart(parameters, MAIN),
    parseFillLookupItemPart(parameters, KNOT),
)