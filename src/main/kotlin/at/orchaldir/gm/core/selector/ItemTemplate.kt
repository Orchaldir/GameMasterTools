package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemTemplateId

fun State.canDelete(itemTemplate: ItemTemplateId) = getItems(itemTemplate).isEmpty()

