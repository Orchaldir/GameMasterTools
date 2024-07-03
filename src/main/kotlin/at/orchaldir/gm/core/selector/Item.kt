package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.appearance.getAvailableEquipmentSlots
import at.orchaldir.gm.core.model.item.*

fun State.canEquip(itemId: ItemId): Boolean {
    val item = items.getOrThrow(itemId)

    return itemTemplates.getOrThrow(item.template).canEquip()
}

fun State.canEquip(character: CharacterId, itemId: ItemId): Boolean {
    val item = items.getOrThrow(itemId)

    return canEquip(character, item)
}

fun State.canEquip(character: CharacterId, item: Item): Boolean {
    if (item.isEquippedBy(character)) {
        return true
    }

    return canEquip(character, item.template)
}

fun State.canEquip(character: CharacterId, templateId: ItemTemplateId): Boolean {
    val template = itemTemplates.getOrThrow(templateId)
    val freeSlots = getFreeSlots(character)

    return template.canEquip() && freeSlots.containsAll(template.slots)
}

fun State.getEquippedItems(character: CharacterId) = items.getAll()
    .filter { it.isEquippedBy(character) }

fun State.getEquippedSlots(character: CharacterId) = items.getAll()
    .flatMap {
        if (it.isEquippedBy(character)) {
            itemTemplates.getOrThrow(it.template).slots
        } else {
            emptySet()
        }
    }.toSet()

fun State.getFreeSlots(character: CharacterId): Set<EquipmentSlot> {
    val availableSlots = characters.getOrThrow(character).appearance.getAvailableEquipmentSlots()
    return availableSlots - getEquippedSlots(character)
}

fun State.getInventory(character: CharacterId) = items.getAll()
    .filter {
        when (it.location) {
            is InInventory -> it.location.character == character
            else -> false
        }
    }

fun State.getItems(itemTemplate: ItemTemplateId) = items.getAll()
    .filter { it.template == itemTemplate }

fun State.getName(itemId: ItemId): String {
    val item = items.getOrThrow(itemId)
    val template = itemTemplates.getOrThrow(item.template)

    return template.name
}