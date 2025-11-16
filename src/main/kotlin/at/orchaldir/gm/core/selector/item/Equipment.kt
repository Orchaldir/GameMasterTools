package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.culture.getFashions

fun State.canDeleteEquipment(equipment: EquipmentId) = DeleteResult(equipment)
    .addElements(getCharactersWith(equipment))
    .addElements(getFashions(equipment))

// count

fun State.countEquipment(material: MaterialId) = getEquipmentStorage()
    .getAll()
    .count { it.data.contains(material) }

fun State.countEquipment(scheme: ColorSchemeId) = getEquipmentStorage()
    .getAll()
    .count { it.colorSchemes.contains(scheme) }

fun State.countEquippedWith(scheme: ColorSchemeId) = getCharacterStorage()
    .getAll()
    .count {
        it.equipmentMap
            .getAllEquipment()
            .any { pair -> pair.second == scheme }
    }

//

fun State.isAvailable(set: ClothingSet) = set.getTypes()
    .all { isAvailable(it) }

fun State.isAvailable(type: EquipmentDataType) = getEquipmentStorage()
    .getAll()
    .any { it.data.isType(type) }

// get

fun State.getEquipmentOf(type: EquipmentDataType) = getEquipmentStorage().getAll()
    .filter { it.data.isType(type) }

fun State.getEquipment(scheme: ColorSchemeId) = getEquipmentStorage()
    .getAll()
    .filter { it.colorSchemes.contains(scheme) }

fun State.getEquipmentMadeOf(material: MaterialId) = getEquipmentStorage().getAll()
    .filter { it.data.contains(material) }

fun State.getEquipmentId(type: EquipmentDataType) = getEquipmentOf(type)
    .map { it.id() }
    .toSet()

fun State.getEquipment(character: CharacterId) =
    getEquipment(getCharacterStorage().getOrThrow(character))

fun State.getEquipment(character: Character) = getEquipment(character.equipmentMap)

fun State.getEquipment(equipmentMap: EquipmentIdMap) = equipmentMap.convert { pair ->
    Pair(
        getEquipmentStorage().getOrThrow(pair.first).data,
        getColorSchemeStorage().getOrThrow(pair.second).data,
    )
}

fun State.getEquippedBy(equipment: EquipmentId) = getCharacterStorage()
    .getAll()
    .filter {
        it.equipmentMap
            .getAllEquipment()
            .any { pair -> pair.first == equipment }
    }

fun State.getEquippedWith(scheme: ColorSchemeId) = getCharacterStorage()
    .getAll()
    .filter {
        it.equipmentMap
            .getAllEquipment()
            .any { pair -> pair.second == scheme }
    }

// stats

fun State.getArmors(modifier: EquipmentModifierId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getArmorStats()?.modifiers?.contains(modifier) ?: false }

fun State.getArmors(type: ArmorTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getArmorStats()?.type == type }

fun State.getMeleeWeapons(modifier: EquipmentModifierId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getMeleeWeaponStats()?.modifiers?.contains(modifier) ?: false }

fun State.getMeleeWeapons(type: MeleeWeaponTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getMeleeWeaponStats()?.type == type }

fun State.getShields(modifier: EquipmentModifierId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getShieldStats()?.modifiers?.contains(modifier) ?: false }

fun State.getShields(type: ShieldTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getShieldStats()?.type == type }
