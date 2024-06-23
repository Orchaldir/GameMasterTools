package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemId
import at.orchaldir.gm.core.model.item.ItemTemplateId

fun State.canDelete(item: ItemId) = true

fun State.getItems(itemTemplate: ItemTemplateId) = items.getAll()
    .filter { it.template == itemTemplate }