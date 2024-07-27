package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ItemTemplateId

fun State.canDelete(fashion: FashionId) = getCultures(fashion).isEmpty()

fun State.getFashions(id: ItemTemplateId): List<Fashion> {
    val itemTemplate = itemTemplates.getOrThrow(id)

    return fashion.getAll()
        .filter { it.getOptions(itemTemplate.equipment.getType()).isAvailable(id) }
}
