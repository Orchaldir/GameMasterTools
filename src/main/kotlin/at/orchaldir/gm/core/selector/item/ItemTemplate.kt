package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentType
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId
import at.orchaldir.gm.core.model.material.MaterialId

fun State.canDelete(itemTemplate: ItemTemplateId) = getCharacterStorage().getAll()
    .none { it.equipmentMap.contains(itemTemplate) }

fun State.countItemTemplates(material: MaterialId) = getItemTemplateStorage()
    .getAll()
    .count { it.equipment.contains(material) }

fun State.getItemTemplatesOf(type: EquipmentType) = getItemTemplateStorage().getAll()
    .filter { it.equipment.isType(type) }

fun State.getItemTemplatesMadeOf(material: MaterialId) = getItemTemplateStorage().getAll()
    .filter { it.equipment.contains(material) }

fun State.getItemTemplatesId(type: EquipmentType) = getItemTemplatesOf(type)
    .map { it.id() }
    .toSet()

fun State.getEquipment(character: CharacterId) =
    getEquipment(getCharacterStorage().getOrThrow(character))

fun State.getEquipment(character: Character) = getEquipment(character.equipmentMap)

fun State.getEquipment(equipmentMap: EquipmentMap) = equipmentMap
    .map
    .values
    .map { getItemTemplateStorage().getOrThrow(it).equipment }

fun State.getEquippedBy(itemTemplate: ItemTemplateId) = getCharacterStorage().getAll()
    .filter { it.equipmentMap.contains(itemTemplate) }

