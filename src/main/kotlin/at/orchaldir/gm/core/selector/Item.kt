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

    return canEquip(character, item) == null
}

fun State.canEquip(character: CharacterId, item: Item): String? {
    val template = itemTemplates.get(item.template) ?: return "Unknown template ${item.template.value}"

    if (!template.canEquip()) {
        return "Template cannot be equipped"
    }

    val availableSlots = characters.get(character)?.appearance?.getAvailableEquipmentSlots()
        ?: return "Unknown character ${character.value}"

    val slots = template.slots()

    if (!availableSlots.containsAll(slots)) {
        return "Character doesn't have equipment slots"
    }

    val slotToItemMap = getEquippedSlots(character)

    if (slots.all { slot -> canEquip(slotToItemMap, slot, item) }) {
        return null
    }

    return "Character's equipment slots are full"
}

private fun canEquip(slotToItemMap: Map<EquipmentSlot, ItemId>, slot: EquipmentSlot, item: Item): Boolean {
    val itemId = slotToItemMap[slot] ?: return true

    return item.id == itemId
}

fun State.getEquippedItems(character: CharacterId) = items.getAll()
    .filter { it.isEquippedBy(character) }

fun State.getEquippedItems(itemTemplate: ItemTemplateId) = getItems(itemTemplate)
    .filter { it.location is EquippedItem }

fun State.getEquipment(character: CharacterId) = getEquippedItems(character)
    .map { itemTemplates.getOrThrow(it.template).equipment }

fun State.getEquippedSlots(character: CharacterId): Map<EquipmentSlot, ItemId> {
    val map = mutableMapOf<EquipmentSlot, ItemId>()

    items.getAll().forEach { item ->
        if (item.isEquippedBy(character)) {
            itemTemplates.getOrThrow(item.template)
                .slots()
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