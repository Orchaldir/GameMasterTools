package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.item.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseItemTemplateId(parameters: Parameters, param: String) = ItemTemplateId(parameters[param]?.toInt() ?: 0)

fun parseItemTemplate(id: ItemTemplateId, parameters: Parameters): ItemTemplate {
    val name = parameters.getOrFail(NAME)

    return ItemTemplate(id, name, parseEquipment(parameters))
}

fun parseEquipment(parameters: Parameters) = when (parse(parameters, EQUIPMENT_TYPE, EquipmentType.None)) {
    EquipmentType.None -> NoEquipment
    EquipmentType.Pants -> Pants(
        parse(parameters, EQUIPMENT_STYLE, PantsStyle.Regular),
        parse(parameters, EQUIPMENT_COLOR, Color.SaddleBrown),
    )
    EquipmentType.Shirt -> Shirt(
        parse(parameters, NECKLINE_STYLE, NecklineStyle.None),
        parse(parameters, SLEEVE_STYLE, SleeveStyle.Long),
        parse(parameters, EQUIPMENT_COLOR, Color.SaddleBrown),
    )
}
