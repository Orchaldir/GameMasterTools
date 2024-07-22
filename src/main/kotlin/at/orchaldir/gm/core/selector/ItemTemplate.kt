package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId

fun State.canDelete(itemTemplate: ItemTemplateId) = getItems(itemTemplate).isEmpty()

fun State.getItemTemplates(type: EquipmentType) = itemTemplates.getAll()
        .filter { it.equipment.isType(type) }

fun State.getItemTemplatesId(type: EquipmentType) = getItemTemplates(type)
        .map { it.id() }
        .toSet()

