package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.editFillItemPart
import at.orchaldir.gm.app.html.item.parseFillItemPart
import at.orchaldir.gm.app.html.item.showFillItemPart
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

        is Gloves -> {
            field("Style", data.style)
            showFillItemPart(call, state, data.main, "Main")
        }

        is Hat -> {
            field("Style", data.style)
            showFillItemPart(call, state, data.main, "Main")
        }

        is Necklace -> showNecklace(call, state, data)

        is Pants -> {
            field("Style", data.style)
            showFillItemPart(call, state, data.main, "Main")
        }

        is Shirt -> {
            field("Neckline Style", data.necklineStyle)
            field("Sleeve Style", data.sleeveStyle)
            showFillItemPart(call, state, data.main, "Main")
        }

        is Skirt -> {
            field("Style", data.style)
            showFillItemPart(call, state, data.main, "Main")
        }

        is Socks -> {
            field("Style", data.style)
            showFillItemPart(call, state, data.main, "Main")
        }

        is SuitJacket -> showSuitJacket(call, state, data)

        is Tie -> {
            field("Style", data.style)
            field("Size", data.size)
            showFillItemPart(call, state, data.main, "Main")
            showFillItemPart(call, state, data.knot, "Knot")
        }
    }
}

// edit

fun FORM.editEquipment(
    state: State,
    equipment: Equipment,
) {
    selectName(equipment.name)
    selectWeight("Weight", WEIGHT, equipment.weight, 10, 10000, SiPrefix.Base)
    selectElements(
        state,
        "Color Schemas",
        combine(COLOR, SCHEME),
        state.sortColorSchemes(),
        equipment.colorSchemes,
    )
    selectValue(
        "Equipment",
        combine(EQUIPMENT, TYPE),
        EquipmentDataType.entries,
        equipment.data.getType(),
    )

    editEquipmentData(state, equipment)
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

        is Gloves -> {
            selectValue("Style", GLOVES, GloveStyle.entries, data.style)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Hat -> {
            selectValue("Style", HAT, HatStyle.entries, data.style)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Necklace -> editNecklace(state, data)

        is Pants -> {
            selectValue("Style", PANTS, PantsStyle.entries, data.style)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Shirt -> {
            selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
            selectSleeveStyle(
                SleeveStyle.entries,
                data.sleeveStyle,
            )
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Skirt -> {
            selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, data.style)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Socks -> {
            selectValue("Style", STYLE, SocksStyle.entries, data.style)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is SuitJacket -> editSuitJacket(state, data)

        is Tie -> {
            selectValue("Style", STYLE, TieStyle.entries, data.style)
            selectValue("Size", SIZE, Size.entries, data.size)
            editFillItemPart(state, data.main, MAIN, "Main")
            editFillItemPart(state, data.knot, KNOT, "Knot")
        }
    }
}

// parse

fun parseEquipmentId(value: String) = EquipmentId(value.toInt())

fun parseEquipmentId(parameters: Parameters, param: String) = EquipmentId(parseInt(parameters, param))

fun parseEquipment(id: EquipmentId, parameters: Parameters) = Equipment(
    id,
    parseName(parameters),
    parseEquipmentData(parameters),
    parseWeight(parameters, WEIGHT, SiPrefix.Base),
    parseElements(
        parameters,
        combine(COLOR, SCHEME),
        ::parseColorSchemeId,
    ),
)

fun parseEquipmentData(parameters: Parameters) =
    when (parse(parameters, combine(EQUIPMENT, TYPE), EquipmentDataType.Belt)) {
        EquipmentDataType.Belt -> parseBelt(parameters)
        EquipmentDataType.Coat -> parseCoat(parameters)
        EquipmentDataType.Dress -> parseDress(parameters)
        EquipmentDataType.Earring -> parseEarring(parameters)
        EquipmentDataType.EyePatch -> parseEyePatch(parameters)
        EquipmentDataType.Footwear -> parseFootwear(parameters)
        EquipmentDataType.Glasses -> parseGlasses(parameters)

        EquipmentDataType.Gloves -> Gloves(
            parse(parameters, GLOVES, GloveStyle.Hand),
            parseFillItemPart(parameters, MAIN),
        )

        EquipmentDataType.Hat -> Hat(
            parse(parameters, HAT, HatStyle.TopHat),
            parseFillItemPart(parameters, MAIN),
        )

        EquipmentDataType.Necklace -> parseNecklace(parameters)

        EquipmentDataType.Pants -> Pants(
            parse(parameters, PANTS, PantsStyle.Regular),
            parseFillItemPart(parameters, MAIN),
        )

        EquipmentDataType.Shirt -> parseShirt(parameters)

        EquipmentDataType.Skirt -> Skirt(
            parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
            parseFillItemPart(parameters, MAIN),
        )

        EquipmentDataType.Socks -> Socks(
            parse(parameters, STYLE, SocksStyle.Quarter),
            parseFillItemPart(parameters, MAIN),
        )

        EquipmentDataType.SuitJacket -> parseSuitJacket(parameters)

        EquipmentDataType.Tie -> parseTie(parameters)
    }

private fun parseShirt(parameters: Parameters): Shirt {
    val neckline = parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.None)

    return Shirt(
        neckline,
        parseSleeveStyle(parameters, neckline),
        parseFillItemPart(parameters, MAIN),
    )
}

private fun parseTie(parameters: Parameters) = Tie(
    parse(parameters, STYLE, TieStyle.Tie),
    parse(parameters, SIZE, Size.Medium),
    parseFillItemPart(parameters, MAIN),
    parseFillItemPart(parameters, KNOT),
)