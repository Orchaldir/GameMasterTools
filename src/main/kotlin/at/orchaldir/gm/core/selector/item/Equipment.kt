package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.material.MaterialId

fun State.canDelete(equipment: EquipmentId) = getCharacterStorage().getAll()
    .none { it.equipmentMap.contains(equipment) }

fun State.countEquipment(material: MaterialId) = getEquipmentStorage()
    .getAll()
    .count { it.data.contains(material) }

fun State.getEquipmentOf(type: EquipmentDataType) = getEquipmentStorage().getAll()
    .filter { it.data.isType(type) }

fun State.getEquipmentMadeOf(material: MaterialId) = getEquipmentStorage().getAll()
    .filter { it.data.contains(material) }

fun State.getEquipmentId(type: EquipmentDataType) = getEquipmentOf(type)
    .map { it.id() }
    .toSet()

fun State.getEquipment(character: CharacterId) =
    getEquipment(getCharacterStorage().getOrThrow(character))

fun State.getEquipment(character: Character) = getEquipment(character.equipmentMap)

fun State.getEquipment(equipmentMap: EquipmentMap<EquipmentId>) = EquipmentMap(
    equipmentMap
    .map
        .mapValues { getEquipmentStorage().getOrThrow(it.value).data })

fun State.getEquippedBy(equipment: EquipmentId) = getCharacterStorage().getAll()
    .filter { it.equipmentMap.contains(equipment) }

