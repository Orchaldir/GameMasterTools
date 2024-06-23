package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.model.item.ItemId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseItem(id: ItemId, parameters: Parameters) = Item(
    id,
    parameters.getOrFail(NAME),
    parseItemTemplateId(parameters, ITEM_TEMPLATE),
)