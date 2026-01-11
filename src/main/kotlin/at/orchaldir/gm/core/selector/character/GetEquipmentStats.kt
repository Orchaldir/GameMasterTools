package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Equipped
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.Protection
import at.orchaldir.gm.core.model.rpg.combat.RangedAttack
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMap
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttacks
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtection
import at.orchaldir.gm.core.selector.rpg.statblock.resolveRangedAttacks

// get armors

fun getArmors(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getArmors(state, state.getEquipmentMap(equipped, lookup))

fun getArmors(state: State, map: EquipmentIdMap): Map<Equipment, Protection> {
    val armorMap = mutableMapOf<Equipment, Protection>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.data.getArmorStats() ?: return@forEach
        val type = state.getArmorTypeStorage().getOptional(stats.type) ?: return@forEach
        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        armorMap[equipment] = resolveProtection(effects, type.protection)
    }

    return armorMap
}

// get melee attacks

fun getMeleeAttacks(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getMeleeAttacks(state, state.getEquipmentMap(equipped, lookup))

fun getMeleeAttacks(state: State, map: EquipmentIdMap): Map<Equipment, List<MeleeAttack>> {
    val meleeAttackMap = mutableMapOf<Equipment, List<MeleeAttack>>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.data.getMeleeWeaponStats() ?: return@forEach
        val type = state.getMeleeWeaponTypeStorage().getOptional(stats.type) ?: return@forEach
        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        meleeAttackMap[equipment] = resolveMeleeAttacks(effects, type.attacks)
    }

    return meleeAttackMap
}

// get melee attacks

fun getRangedAttacks(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getRangedAttacks(state, state.getEquipmentMap(equipped, lookup))

fun getRangedAttacks(state: State, map: EquipmentIdMap): Map<Equipment, List<RangedAttack>> {
    val meleeAttackMap = mutableMapOf<Equipment, List<RangedAttack>>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.data.getRangedWeaponStats() ?: return@forEach
        val type = state.getRangedWeaponTypeStorage().getOptional(stats.type) ?: return@forEach
        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        meleeAttackMap[equipment] = resolveRangedAttacks(effects, type.attacks)
    }

    return meleeAttackMap
}

// get shields

fun getShields(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getShields(state, state.getEquipmentMap(equipped, lookup))

fun getShields(state: State, map: EquipmentIdMap): Map<Equipment, Protection> {
    val armorMap = mutableMapOf<Equipment, Protection>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.data.getShieldStats() ?: return@forEach
        val type = state.getShieldTypeStorage().getOptional(stats.type) ?: return@forEach
        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        armorMap[equipment] = resolveProtection(effects, type.protection)
    }

    return armorMap
}