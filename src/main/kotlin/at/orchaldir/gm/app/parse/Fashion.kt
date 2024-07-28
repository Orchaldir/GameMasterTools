package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.EquipmentType
import io.ktor.http.*
import io.ktor.server.util.*

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parameters[param]?.toInt() ?: 0)

fun parseFashion(id: FashionId, parameters: Parameters): Fashion {
    val name = parameters.getOrFail(NAME)

    return Fashion(
        id,
        name,
        parseOneOf(parameters, CLOTHING_SETS, ClothingSet::valueOf),
        parseSomeOf(parameters, ACCESSORY_RARITY, EquipmentType::valueOf),
        parseItemTemplates(parameters, EquipmentType.Dress),
        parseItemTemplates(parameters, EquipmentType.Footwear),
        parseItemTemplates(parameters, EquipmentType.Gloves),
        parseItemTemplates(parameters, EquipmentType.Hat),
        parseItemTemplates(parameters, EquipmentType.Pants),
        parseItemTemplates(parameters, EquipmentType.Shirt),
        parseItemTemplates(parameters, EquipmentType.Skirt),
    )
}

private fun parseItemTemplates(parameters: Parameters, type: EquipmentType) =
    parseOneOrNone(parameters, type.name, ::parseItemTemplateId)
