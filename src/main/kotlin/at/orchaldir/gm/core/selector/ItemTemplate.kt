package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId

fun State.canDelete(itemTemplate: ItemTemplateId) = characters.getAll()
        .none { it.equipmentMap.contains(itemTemplate) }

fun State.getItemTemplates(type: EquipmentType) = itemTemplates.getAll()
        .filter { it.equipment.isType(type) }

fun State.getItemTemplatesId(type: EquipmentType) = getItemTemplates(type)
        .map { it.id() }
        .toSet()

fun State.getEquipment(character: CharacterId) =
        getEquipment(characters.getOrThrow(character))

fun State.getEquipment(character: Character) = getEquipment(character.equipmentMap)

fun State.getEquipment(equipmentMap: EquipmentMap) = equipmentMap
        .map
        .values
        .map { itemTemplates.getOrThrow(it).equipment }

fun State.getEquippedBy(itemTemplate: ItemTemplateId) = characters.getAll()
        .filter { it.equipmentMap.contains(itemTemplate) }

