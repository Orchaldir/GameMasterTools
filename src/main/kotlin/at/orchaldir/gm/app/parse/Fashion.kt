package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.ACCESSORY_RARITY
import at.orchaldir.gm.app.CLOTHING_SETS
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.item.equipment.parseEquipmentId
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import io.ktor.http.*
import io.ktor.server.util.*

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parseInt(parameters, param))

fun parseFashion(id: FashionId, parameters: Parameters): Fashion {
    val name = parameters.getOrFail(NAME)
    val itemRarityMap = EquipmentDataType.entries
        .associateWith { parseEquipmentMap(parameters, it) }

    return Fashion(
        id,
        name,
        parseOneOf(parameters, CLOTHING_SETS, ClothingSet::valueOf),
        parseSomeOf(parameters, ACCESSORY_RARITY, EquipmentDataType::valueOf),
        itemRarityMap,
    )
}

private fun parseEquipmentMap(parameters: Parameters, type: EquipmentDataType) =
    parseOneOrNone(parameters, type.name, ::parseEquipmentId)
