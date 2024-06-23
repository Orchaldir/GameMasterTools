package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.model.item.ItemId
import io.ktor.http.*

fun parseItem(id: ItemId, parameters: Parameters) = Item(
    id,
    parseItemTemplateId(parameters, ITEM_TEMPLATE),
)