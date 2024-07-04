package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseItemTemplateId(parameters: Parameters, param: String) = ItemTemplateId(parameters[param]?.toInt() ?: 0)

fun parseItemTemplate(id: ItemTemplateId, parameters: Parameters): ItemTemplate {
    val name = parameters.getOrFail(NAME)

    return ItemTemplate(id, name, parseSet(parameters, EQUIPMENT_SLOT))
}
