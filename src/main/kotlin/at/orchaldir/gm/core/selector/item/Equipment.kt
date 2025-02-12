package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.material.MaterialId

fun State.canDelete(itemTemplate: EquipmentId) = getCharacterStorage().getAll()
    .none { it.equipmentMap.contains(itemTemplate) }

fun State.countEquipment(material: MaterialId) = getEquipmentStorage()
    .getAll()
    .count { it.equipment.contains(material) }

fun State.getEquipmentOf(type: EquipmentDataType) = getEquipmentStorage().getAll()
    .filter { it.equipment.isType(type) }

fun State.getEquipmentMadeOf(material: MaterialId) = getEquipmentStorage().getAll()
    .filter { it.equipment.contains(material) }

fun State.getEquipmentId(type: EquipmentDataType) = getEquipmentOf(type)
    .map { it.id() }
    .toSet()

fun State.getEquipment(character: CharacterId) =
    getEquipment(getCharacterStorage().getOrThrow(character))

fun State.getEquipment(character: Character) = getEquipment(character.equipmentMap)

fun State.getEquipment(equipmentMap: EquipmentMap) = equipmentMap
    .map
    .values
    .map { getEquipmentStorage().getOrThrow(it).equipment }

fun State.getEquippedBy(equipment: EquipmentId) = getCharacterStorage().getAll()
    .filter { it.equipmentMap.contains(equipment) }

