package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.item.InInventory
import at.orchaldir.gm.core.model.item.ItemId
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.item.UndefinedItemLocation

fun State.getInventory(character: CharacterId) = items.getAll()
    .filter {
        when (it.location) {
            is InInventory -> it.location.character == character
            UndefinedItemLocation -> false
        }
    }

fun State.getItems(itemTemplate: ItemTemplateId) = items.getAll()
    .filter { it.template == itemTemplate }

fun State.getName(itemId: ItemId): String {
    val item = items.getOrThrow(itemId)
    val template = itemTemplates.getOrThrow(item.template)

    return template.name
}