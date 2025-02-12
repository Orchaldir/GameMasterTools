package at.orchaldir.gm.app.parse.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.util.*

fun parseItemTemplateId(value: String) = EquipmentId(value.toInt())

fun parseItemTemplateId(parameters: Parameters, param: String) = EquipmentId(parseInt(parameters, param))

fun parseItemTemplate(id: EquipmentId, parameters: Parameters): Equipment {
    val name = parameters.getOrFail(NAME)

    return Equipment(id, name, parseEquipment(parameters))
}

fun parseEquipment(parameters: Parameters) =
    when (parse(parameters, combine(EQUIPMENT, TYPE), EquipmentDataType.None)) {
    EquipmentDataType.None -> NoEquipment
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
