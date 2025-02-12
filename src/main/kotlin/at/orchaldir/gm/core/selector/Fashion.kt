package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId

fun State.canDelete(fashion: FashionId) = getCultures(fashion).isEmpty()

fun State.getFashions(id: ItemTemplateId): List<Fashion> {
    val itemTemplate = getItemTemplateStorage().getOrThrow(id)

    return getFashionStorage().getAll()
        .filter { it.getOptions(itemTemplate.equipment.getType()).isAvailable(id) }
}
