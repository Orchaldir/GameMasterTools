package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId

fun State.canDelete(itemTemplate: ItemTemplateId) = getItems(itemTemplate).isEmpty()

fun State.getItemTemplates(type: EquipmentType) = itemTemplates.getAll()
        .filter { it.equipment.isType(type) }

fun State.getItemTemplatesId(type: EquipmentType) = getItemTemplates(type)
        .map { it.id() }
        .toSet()

fun State.getEquipment2(character: CharacterId) =
        getEquipment2(characters.getOrThrow(character))

fun State.getEquipment2(character: Character) = getEquipment2(character.equipmentMap)

fun State.getEquipment2(equipmentMap: EquipmentMap) = equipmentMap
        .map
        .values
        .map { itemTemplates.getOrThrow(it).equipment }

