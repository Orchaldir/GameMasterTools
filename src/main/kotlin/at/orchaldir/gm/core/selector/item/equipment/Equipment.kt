package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.culture.getFashions

fun State.canDeleteEquipment(equipment: EquipmentId) = DeleteResult(equipment)
    .addElements(getCharactersWith(equipment))
    .addElements(getCharacterTemplates(equipment))
    .addElements(getFashions(equipment))

// count

fun State.countEquipment(material: MaterialId) = getEquipmentStorage()
    .getAll()
    .count { it.data.contains(material) }

fun State.countEquipment(scheme: ColorSchemeId) = getEquipmentStorage()
    .getAll()
    .count { it.colorSchemes.contains(scheme) }

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

fun State.getEquippedWith(scheme: ColorSchemeId) = getCharacterStorage()
    .getAll()
    .filter {
        it.equipped.contains(scheme)
    }

// stats

fun State.getEquipment(modifier: EquipmentModifierId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.contains(modifier) }

fun State.getArmors(type: ArmorTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getArmorStats()?.type == type }

fun State.getMeleeWeapons(type: MeleeWeaponTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getMeleeWeaponStats()?.type == type }

fun State.getRangedWeapons(type: RangedWeaponTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getRangedWeaponStats()?.type == type }

fun State.getShields(type: ShieldTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getShieldStats()?.type == type }
