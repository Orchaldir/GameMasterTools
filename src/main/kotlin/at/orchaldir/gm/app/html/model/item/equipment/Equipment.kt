package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.fieldWeight
import at.orchaldir.gm.app.html.model.item.editFillItemPart
import at.orchaldir.gm.app.html.model.item.parseFillItemPart
import at.orchaldir.gm.app.html.model.item.showFillItemPart
import at.orchaldir.gm.app.html.model.parseWeight
import at.orchaldir.gm.app.html.model.selectWeight
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun BODY.showEquipment(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    fieldWeight("Weight", equipment.weight)
    showEquipmentData(call, state, equipment)
}

private fun BODY.showEquipmentData(
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

        is SuitJacket -> doNothing()

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
    val gram = Weight.fromGram(10)
    selectWeight("Weight", WEIGHT, equipment.weight, gram, Weight.fromKilogram(10.0f), gram)
    selectValue(
        "Equipment",
        combine(EQUIPMENT, TYPE),
        EquipmentDataType.entries,
        equipment.data.getType(),
        true
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
            selectValue("Style", GLOVES, GloveStyle.entries, data.style, true)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Hat -> {
            selectValue("Style", HAT, HatStyle.entries, data.style, true)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Necklace -> editNecklace(state, data)

        is Pants -> {
            selectValue("Style", PANTS, PantsStyle.entries, data.style, true)
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
            selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, data.style, true)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is Socks -> {
            selectValue("Style", STYLE, SocksStyle.entries, data.style, true)
            editFillItemPart(state, data.main, MAIN, "Main")
        }

        is SuitJacket -> doNothing()

        is Tie -> {
            selectValue("Style", STYLE, TieStyle.entries, data.style, true)
            selectValue("Size", SIZE, Size.entries, data.size, true)
            editFillItemPart(state, data.main, MAIN, "Main")
            editFillItemPart(state, data.knot, KNOT, "Knot")
        }
    }
}

fun FORM.selectNecklineStyle(options: Collection<NecklineStyle>, current: NecklineStyle) {
    selectValue("Neckline Style", combine(NECKLINE, STYLE), options, current, true)
}

fun FORM.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectValue("Sleeve Style", combine(SLEEVE, STYLE), options, current, true)
}

fun FORM.selectPocketStyle(options: Collection<PocketStyle>, current: PocketStyle) {
    selectValue("Pocket Style", combine(POCKET, STYLE), options, current, true)
}

fun HtmlBlockTag.selectMaterial(
    state: State,
    materialId: MaterialId,
    param: String = MATERIAL,
    label: String = "Material",
) {
    selectElement(state, label, param, state.sortMaterial(), materialId, true)
}

// parse

fun parseEquipmentId(value: String) = EquipmentId(value.toInt())

fun parseEquipmentId(parameters: Parameters, param: String) = EquipmentId(parseInt(parameters, param))

fun parseEquipment(id: EquipmentId, parameters: Parameters): Equipment {
    val name = parameters.getOrFail(NAME)

    return Equipment(
        id,
        name,
        parseEquipmentData(parameters),
        parseWeight(parameters, WEIGHT),
    )
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

        EquipmentDataType.SuitJacket -> TODO()

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

fun parseSleeveStyle(
    parameters: Parameters,
    neckline: NecklineStyle,
) = if (neckline.supportsSleeves()) {
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long)
} else {
    SleeveStyle.None
}

private fun parseTie(parameters: Parameters) = Tie(
    parse(parameters, STYLE, TieStyle.Tie),
    parse(parameters, SIZE, Size.Medium),
    parseFillItemPart(parameters, MAIN),
    parseFillItemPart(parameters, KNOT),
)