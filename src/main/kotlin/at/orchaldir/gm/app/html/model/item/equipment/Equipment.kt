package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.fieldWeight
import at.orchaldir.gm.app.html.model.parseWeight
import at.orchaldir.gm.app.html.model.selectWeight
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
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
        NoEquipment, is Tie -> doNothing()
        is Belt -> showBelt(call, state, data)
        is Coat -> {
            field("Length", data.length)
            field("Neckline Style", data.necklineStyle)
            field("Sleeve Style", data.sleeveStyle)
            showOpeningStyle(data.openingStyle)
            showFill(data.fill)
            fieldLink("Material", call, state, data.material)
        }

        is Dress -> {
            field("Neckline Style", data.necklineStyle)
            field("Skirt Style", data.skirtStyle)
            field("Sleeve Style", data.sleeveStyle)
            showFill(data.fill)
            fieldLink("Material", call, state, data.material)
        }

        is Footwear -> {
            field("Style", data.style)
            field("Color", data.color)
            if (data.style.hasSole()) {
                field("Sole Color", data.sole)
            }
            fieldLink("Material", call, state, data.material)
        }

        is Glasses -> {
            showDetails("Lenses") {
                field("Shape", data.lensShape)
                showFill(data.lensFill)
                fieldLink("Material", call, state, data.lensMaterial)
            }
            showDetails("Frame") {
                field("Type", data.frameType)
                field("Color", data.frameColor)
                fieldLink("Material", call, state, data.frameMaterial)
            }
        }

        is Gloves -> {
            field("Style", data.style)
            showFill(data.fill)
            fieldLink("Material", call, state, data.material)
        }

        is Hat -> {
            field("Style", data.style)
            field("Color", data.color)
            fieldLink("Material", call, state, data.material)
        }

        is Pants -> {
            field("Style", data.style)
            showFill(data.fill)
            fieldLink("Material", call, state, data.material)
        }

        is Shirt -> {
            field("Neckline Style", data.necklineStyle)
            field("Sleeve Style", data.sleeveStyle)
            showFill(data.fill)
            fieldLink("Material", call, state, data.material)
        }

        is Skirt -> {
            field("Style", data.style)
            showFill(data.fill)
            fieldLink("Material", call, state, data.material)
        }
    }
}

private fun BODY.showOpeningStyle(openingStyle: OpeningStyle) {
    field("Opening Style", openingStyle.javaClass.simpleName)
    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> showButtons(openingStyle.buttons)
        is DoubleBreasted -> {
            showButtons(openingStyle.buttons)
            field("Space between Columns", openingStyle.spaceBetweenColumns)
        }

        is Zipper -> {
            field("Zipper Color", openingStyle.color)
        }
    }
}

private fun BODY.showButtons(buttonColumn: ButtonColumn) {
    field("Button Count", buttonColumn.count.toString())
    field("Button Color", buttonColumn.button.color)
    field("Button Size", buttonColumn.button.size)
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
        NoEquipment, is Tie -> doNothing()
        is Belt -> editBelt(state, data)
        is Coat -> {
            selectValue("Length", LENGTH, OuterwearLength.entries, data.length, true)
            selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
            selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
            selectOpeningStyle(data.openingStyle)
            selectFill(data.fill)
            selectMaterial(state, data.material)
        }

        is Dress -> {
            selectNecklineStyle(NecklineStyle.entries, data.necklineStyle)
            selectValue("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, data.skirtStyle, true)
            selectSleeveStyle(
                data.necklineStyle.getSupportsSleevesStyles(),
                data.sleeveStyle,
            )
            selectFill(data.fill)
            selectMaterial(state, data.material)
        }

        is Footwear -> {
            selectValue("Style", FOOTWEAR, FootwearStyle.entries, data.style, true)
            selectColor(data.color, EQUIPMENT_COLOR_0)
            if (data.style.hasSole()) {
                selectColor(data.sole, EQUIPMENT_COLOR_1, "Sole Color")
            }
            selectMaterial(state, data.material)
        }

        is Glasses -> {
            showDetails("Lenses", true) {
                selectValue("Shape", SHAPE, LensShape.entries, data.lensShape, true)
                selectFill(data.lensFill)
                selectMaterial(state, data.lensMaterial, combine(SHAPE, MATERIAL))
            }
            showDetails("Frame", true) {
                selectValue("Shape", FRAME, FrameType.entries, data.frameType, true)
                selectColor(data.frameColor, selectId = combine(FRAME, COLOR))
                selectMaterial(state, data.frameMaterial, combine(FRAME, MATERIAL))
            }
        }

        is Gloves -> {
            selectValue("Style", GLOVES, GloveStyle.entries, data.style, true)
            selectFill(data.fill)
            selectMaterial(state, data.material)
        }

        is Hat -> {
            selectValue("Style", HAT, HatStyle.entries, data.style, true)
            selectColor(data.color, EQUIPMENT_COLOR_0)
            selectMaterial(state, data.material)
        }

        is Pants -> {
            selectValue("Style", PANTS, PantsStyle.entries, data.style, true)
            selectFill(data.fill)
            selectMaterial(state, data.material)
        }

        is Shirt -> {
            selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
            selectSleeveStyle(
                SleeveStyle.entries,
                data.sleeveStyle,
            )
            selectFill(data.fill)
            selectMaterial(state, data.material)
        }

        is Skirt -> {
            selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, data.style, true)
            selectFill(data.fill)
            selectMaterial(state, data.material)
        }
    }
}

private fun FORM.selectNecklineStyle(options: Collection<NecklineStyle>, current: NecklineStyle) {
    selectValue("Neckline Style", NECKLINE_STYLE, options, current, true)
}

private fun FORM.selectOpeningStyle(openingStyle: OpeningStyle) {
    selectValue("Opening Style", OPENING_STYLE, OpeningType.entries, openingStyle.getType(), true)

    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> selectButtons(openingStyle.buttons)
        is DoubleBreasted -> {
            selectButtons(openingStyle.buttons)
            selectValue(
                "Space between Columns",
                SPACE_BETWEEN_COLUMNS,
                Size.entries,
                openingStyle.spaceBetweenColumns,
                true
            )
        }

        is Zipper -> selectColor(openingStyle.color, ZIPPER, "Zipper Color")
    }
}

private fun FORM.selectButtons(buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, BUTTON_COUNT, true)
    selectColor(buttonColumn.button.color, BUTTON_COLOR, "Button Color")
    selectValue("Button Size", BUTTON_SIZE, Size.entries, buttonColumn.button.size, true)
}

private fun FORM.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectValue("Sleeve Style", SLEEVE_STYLE, options, current, true)
}

fun HtmlBlockTag.selectMaterial(
    state: State,
    materialId: MaterialId,
    param: String = MATERIAL,
) {
    selectElement(state, "Material", param, state.sortMaterial(), materialId)
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
    when (parse(parameters, combine(EQUIPMENT, TYPE), EquipmentDataType.None)) {
        EquipmentDataType.None, EquipmentDataType.Tie -> NoEquipment
        EquipmentDataType.Belt -> parseBelt(parameters)
        EquipmentDataType.Coat -> Coat(
            parse(parameters, LENGTH, OuterwearLength.Hip),
            parse(parameters, NECKLINE_STYLE, NecklineStyle.DeepV),
            parse(parameters, SLEEVE_STYLE, SleeveStyle.Long),
            parseOpeningStyle(parameters),
            parseFill(parameters),
            parseMaterialId(parameters, MATERIAL),
        )

        EquipmentDataType.Dress -> parseDress(parameters)

        EquipmentDataType.Footwear -> Footwear(
            parse(parameters, FOOTWEAR, FootwearStyle.Shoes),
            parse(parameters, EQUIPMENT_COLOR_0, Color.SaddleBrown),
            parse(parameters, EQUIPMENT_COLOR_1, Color.SaddleBrown),
            parseMaterialId(parameters, MATERIAL),
        )

        EquipmentDataType.Glasses -> Glasses(
            parse(parameters, SHAPE, LensShape.Rectangle),
            parse(parameters, FRAME, FrameType.FullRimmed),
            parseFill(parameters),
            parse(parameters, combine(FRAME, COLOR), Color.Navy),
            parseMaterialId(parameters, combine(SHAPE, MATERIAL)),
            parseMaterialId(parameters, combine(FRAME, MATERIAL)),
        )

        EquipmentDataType.Gloves -> Gloves(
            parse(parameters, GLOVES, GloveStyle.Hand),
            parseFill(parameters),
            parseMaterialId(parameters, MATERIAL),
        )

        EquipmentDataType.Hat -> Hat(
            parse(parameters, HAT, HatStyle.TopHat),
            parse(parameters, EQUIPMENT_COLOR_0, Color.SaddleBrown),
            parseMaterialId(parameters, MATERIAL),
        )

        EquipmentDataType.Pants -> Pants(
            parse(parameters, PANTS, PantsStyle.Regular),
            parseFill(parameters),
            parseMaterialId(parameters, MATERIAL),
        )

        EquipmentDataType.Shirt -> parseShirt(parameters)

        EquipmentDataType.Skirt -> Skirt(
            parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
            parseFill(parameters),
            parseMaterialId(parameters, MATERIAL),
        )
    }

private fun parseDress(parameters: Parameters): Dress {
    val neckline = parse(parameters, NECKLINE_STYLE, NecklineStyle.None)

    return Dress(
        neckline,
        parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
        parseSleeveStyle(parameters, neckline),
        parseFill(parameters),
        parseMaterialId(parameters, MATERIAL),
    )
}

private fun parseShirt(parameters: Parameters): Shirt {
    val neckline = parse(parameters, NECKLINE_STYLE, NecklineStyle.None)

    return Shirt(
        neckline,
        parseSleeveStyle(parameters, neckline),
        parseFill(parameters),
        parseMaterialId(parameters, MATERIAL),
    )
}

private fun parseOpeningStyle(parameters: Parameters): OpeningStyle {
    val type = parse(parameters, OPENING_STYLE, OpeningType.NoOpening)

    return when (type) {
        OpeningType.NoOpening -> NoOpening
        OpeningType.SingleBreasted -> SingleBreasted(parseButtonColumn(parameters))
        OpeningType.DoubleBreasted -> DoubleBreasted(
            parseButtonColumn(parameters),
            parse(parameters, SPACE_BETWEEN_COLUMNS, Size.Medium)
        )

        OpeningType.Zipper -> Zipper(parse(parameters, ZIPPER, Color.Silver))
    }
}

private fun parseButtonColumn(parameters: Parameters) = ButtonColumn(
    Button(
        parse(parameters, BUTTON_SIZE, Size.Medium),
        parse(parameters, BUTTON_COLOR, Color.Silver)
    ),
    parameters[BUTTON_COUNT]?.toUByte() ?: 1u,
)

private fun parseSleeveStyle(
    parameters: Parameters,
    neckline: NecklineStyle,
) = if (neckline.supportsSleeves()) {
    parse(parameters, SLEEVE_STYLE, SleeveStyle.Long)
} else {
    SleeveStyle.None
}