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
    val template = itemTemplates.getOrThrow(item.template)

    if (!template.canEquip()) {
        return false
    }

    val availableSlots = characters.getOrThrow(character).appearance.getAvailableEquipmentSlots()

    if (!availableSlots.containsAll(template.slots)) {
        return false
    }

    val slotToItemMap = getEquippedSlots(character)

    return template.slots.none { slot -> canEquip(slotToItemMap, slot, item) }
}

private fun canEquip(slotToItemMap: Map<EquipmentSlot, ItemId>, slot: EquipmentSlot, item: Item): Boolean {
    val itemId = slotToItemMap[slot] ?: return true

    return item.id == itemId
}

fun State.getEquippedItems(character: CharacterId) = items.getAll()
    .filter { it.isEquippedBy(character) }

fun State.getEquippedSlots(character: CharacterId): Map<EquipmentSlot, ItemId> {
    val map = mutableMapOf<EquipmentSlot, ItemId>()

    items.getAll().forEach { item ->
        if (item.isEquippedBy(character)) {
            itemTemplates.getOrThrow(item.template)
                .slots
                .forEach { slot -> map[slot] = item.id }
        }
    }

    return map
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