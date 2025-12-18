package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.rpg.combat.ArmorTypeId
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponTypeId
import at.orchaldir.gm.core.model.rpg.combat.ShieldTypeId
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.render.UndefinedColors
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

fun State.getEquipment(character: CharacterId) =
    getEquipment(getCharacterStorage().getOrThrow(character))

fun State.getEquipment(character: Character) = getEquipment(character.equipped, character.statblock)

fun State.getEquipment(
    equipped: Equipped,
    lookup: StatblockLookup,
) = resolveEquipment(getEquipmentMap(equipped, lookup))

fun State.getEquipmentMap(
    equipped: Equipped,
    lookup: StatblockLookup,
): EquipmentIdMap = when (equipped) {
    is EquippedEquipment -> equipped.map
    is EquippedUniform -> {
        val uniform = getUniformStorage().getOrThrow(equipped.uniform)
        uniform.equipmentMap
    }
    is UseEquipmentFromTemplate -> {
        val templateId = lookup.template() ?: error("Cannot get equipment from the template without a template!")
        val template = getCharacterTemplateStorage().getOrThrow(templateId)
        getEquipmentMap(template.equipped, template.statblock)
    }

    UndefinedEquipped -> EquipmentIdMap()
}

fun State.resolveEquipment(idMap: EquipmentIdMap) = idMap.convert { pair ->
    Pair(
        getEquipmentStorage().getOrThrow(pair.first).data,
        getColorSchemeStorage().getOptional(pair.second)?.data ?: UndefinedColors,
    )
}

fun State.getEquippedBy(equipment: EquipmentId) = getCharacterStorage()
    .getAll()
    .filter {
        it.equipped.contains(equipment)
    }

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

fun State.getShields(type: ShieldTypeId) = getEquipmentStorage()
    .getAll()
    .filter { it.data.getShieldStats()?.type == type }
