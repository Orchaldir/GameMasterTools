package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.appearance.*
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.model.item.style.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseItemTemplateId(value: String) = ItemTemplateId(value.toInt())

fun parseItemTemplateId(parameters: Parameters, param: String) = ItemTemplateId(parameters[param]?.toInt() ?: 0)

fun parseItemTemplate(id: ItemTemplateId, parameters: Parameters): ItemTemplate {
    val name = parameters.getOrFail(NAME)

    return ItemTemplate(id, name, parseEquipment(parameters))
}

fun parseEquipment(parameters: Parameters) = when (parse(parameters, EQUIPMENT_TYPE, EquipmentType.None)) {
    EquipmentType.None -> NoEquipment
    EquipmentType.Dress -> parseDress(parameters)

    EquipmentType.Footwear -> Footwear(
        parse(parameters, FOOTWEAR, FootwearStyle.Shoes),
        parse(parameters, EQUIPMENT_COLOR_0, Color.SaddleBrown),
        parse(parameters, EQUIPMENT_COLOR_1, Color.SaddleBrown),
        parseMaterialId(parameters, MATERIAL),
    )

    EquipmentType.Gloves -> Gloves(
        parse(parameters, GLOVES, GloveStyle.Hand),
        parseFill(parameters),
        parseMaterialId(parameters, MATERIAL),
    )

    EquipmentType.Hat -> Hat(
        parse(parameters, HAT, HatStyle.TopHat),
        parse(parameters, EQUIPMENT_COLOR_0, Color.SaddleBrown),
        parseMaterialId(parameters, MATERIAL),
    )

    EquipmentType.Pants -> Pants(
        parse(parameters, PANTS, PantsStyle.Regular),
        parseFill(parameters),
        parseMaterialId(parameters, MATERIAL),
    )

    EquipmentType.Shirt -> parseShirt(parameters)

    EquipmentType.Skirt -> Skirt(
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

private fun parseFill(parameters: Parameters): Fill {
    val type = parse(parameters, FILL_TYPE, FillType.Solid)

    return when (type) {
        FillType.Solid -> Solid(parse(parameters, EQUIPMENT_COLOR_0, Color.SkyBlue))
        FillType.VerticalStripes -> VerticalStripes(
            parse(parameters, EQUIPMENT_COLOR_0, Color.Black),
            parse(parameters, EQUIPMENT_COLOR_1, Color.White),
            parseWidth(parameters),
        )

        FillType.HorizontalStripes -> HorizontalStripes(
            parse(parameters, EQUIPMENT_COLOR_0, Color.Black),
            parse(parameters, EQUIPMENT_COLOR_1, Color.White),
            parseWidth(parameters),
        )
    }
}

private fun parseWidth(parameters: Parameters) = parameters[PATTERN_WIDTH]?.toUByte() ?: 1u

private fun parseSleeveStyle(
    parameters: Parameters,
    neckline: NecklineStyle,
) = if (neckline.supportsSleeves()) {
    parse(parameters, SLEEVE_STYLE, SleeveStyle.None)
} else {
    SleeveStyle.None
}
