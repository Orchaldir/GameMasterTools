package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearanceType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentTypeId
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifierId
import at.orchaldir.gm.core.model.util.render.ColorSchemeGroupId
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.culture.getFashions
import at.orchaldir.gm.core.selector.gm.treasure.getTreasureParcelsWith
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentType

fun State.canDeleteEquipment(equipment: EquipmentId) = DeleteResult(equipment)
    .addElements(getCharactersWith(equipment))
    .addElements(getCharacterTemplates(equipment))
    .addElements(getFashions(equipment))
    .addElements(getTreasureParcelsWith(equipment))

// count

fun State.countEquipment(material: MaterialId) = getEquipmentStorage()
    .getAll()
    .count { it.appearance.contains(material) }

fun State.countEquipment(scheme: ColorSchemeId) = getEquipmentStorage()
    .getAll()
    .count { it.colorSchemes.contains(scheme) }

//

fun State.isAvailable(set: ClothingSet) = set.getTypes()
    .all { isAvailable(it) }

fun State.isAvailable(type: EquipmentAppearanceType) = getEquipmentStorage()
    .getAll()
    .any { it.appearance.isType(type) }

// get

fun State.getEquipmentOf(type: EquipmentAppearanceType) = getEquipmentStorage().getAll()
    .filter { it.appearance.isType(type) }

fun State.getEquipment(scheme: ColorSchemeId) = getEquipmentStorage()
    .getAll()
    .filter { it.colorSchemes.contains(scheme) }

fun State.getEquipment(group: ColorSchemeGroupId) = getEquipmentStorage()
    .getAll()
    .filter { it.colorSchemes.contains(group) }

fun State.getEquipmentMadeOf(material: MaterialId) = getEquipmentStorage().getAll()
    .filter { it.appearance.contains(material) }

fun State.getEquipmentId(type: EquipmentAppearanceType) = getEquipmentOf(type)
    .map { it.id() }
    .toSet()

fun State.getEquippedWith(scheme: ColorSchemeId) = getCharacterStorage()
    .getAll()
    .filter {
        it.equipped.contains(scheme)
    }

// stats

fun State.getEquipmentWithMeleeAttacks() = getEquipmentStorage()
    .getAll()
    .filter { getEquipmentType(it)?.meleeAttacks?.isNotEmpty() ?: false }

fun State.getEquipment(modifier: EquipmentModifierId) = getEquipmentStorage()
    .getAll()
    .filter { it.stats.modifiers.contains(modifier) }

fun State.getEquipment(type: EquipmentTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.stats.type == type }
