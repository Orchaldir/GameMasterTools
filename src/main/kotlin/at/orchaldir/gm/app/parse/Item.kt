package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.item.*
import io.ktor.http.*

fun parseItem(id: ItemId, parameters: Parameters) = Item(
    id,
    parseItemTemplateId(parameters, ITEM_TEMPLATE),
    parseLocation(parameters),
)

private fun parseLocation(parameters: Parameters) = when (parse(parameters, LOCATION, ItemLocationType.Undefined)) {
    ItemLocationType.Inventory -> InInventory(parseCharacterId(parameters, INVENTORY))
    ItemLocationType.Undefined -> UndefinedItemLocation
}