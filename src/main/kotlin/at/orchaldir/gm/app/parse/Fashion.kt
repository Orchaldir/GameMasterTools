package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.NOT_NONE
import io.ktor.http.*
import io.ktor.server.util.*

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parseInt(parameters, param))

fun parseFashion(id: FashionId, parameters: Parameters): Fashion {
    val name = parameters.getOrFail(NAME)
    val itemRarityMap = NOT_NONE
        .associateWith { parseItemTemplates(parameters, it) }

    return Fashion(
        id,
        name,
        parseOneOf(parameters, CLOTHING_SETS, ClothingSet::valueOf),
        parseSomeOf(parameters, ACCESSORY_RARITY, EquipmentType::valueOf),
        itemRarityMap,
    )
}

private fun parseItemTemplates(parameters: Parameters, type: EquipmentType) =
    parseOneOrNone(parameters, type.name, ::parseItemTemplateId)
