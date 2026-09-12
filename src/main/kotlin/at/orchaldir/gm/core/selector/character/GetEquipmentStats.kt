package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Equipped
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.Protection
import at.orchaldir.gm.core.model.rpg.combat.RangedAttack
import at.orchaldir.gm.core.model.rpg.combat.UndefinedProtection
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentIdMap
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttacks
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtection
import at.orchaldir.gm.core.selector.rpg.statblock.resolveRangedAttacks

// get melee attacks

fun getMeleeAttacks(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getMeleeAttacks(state, state.getEquipmentIdMap(equipped, lookup))

fun getMeleeAttacks(state: State, map: EquipmentIdMap): Map<Equipment, List<MeleeAttack>> {
    val meleeAttackMap = mutableMapOf<Equipment, List<MeleeAttack>>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.stats
        val type = state.getEquipmentTypeStorage().getOptional(stats.type) ?: return@forEach

        if (type.meleeAttacks.isEmpty()) {
            return@forEach
        }

        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        meleeAttackMap[equipment] = resolveMeleeAttacks(state, effects, type.meleeAttacks)
    }

    return meleeAttackMap
}

// get protection

fun getProtection(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getProtection(state, state.getEquipmentIdMap(equipped, lookup))

fun getProtection(state: State, map: EquipmentIdMap): Map<Equipment, Protection> {
    val armorMap = mutableMapOf<Equipment, Protection>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.stats
        val type = state.getEquipmentTypeStorage().getOptional(stats.type) ?: return@forEach

        if (type.protection !is UndefinedProtection) {
            return@forEach
        }

        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        armorMap[equipment] = resolveProtection(effects, type.protection)
    }

    return armorMap
}

// get ranged attacks

fun getRangedAttacks(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = getRangedAttacks(state, state.getEquipmentIdMap(equipped, lookup))

fun getRangedAttacks(state: State, map: EquipmentIdMap): Map<Equipment, List<RangedAttack>> {
    val meleeAttackMap = mutableMapOf<Equipment, List<RangedAttack>>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.stats
        val type = state.getEquipmentTypeStorage().getOptional(stats.type) ?: return@forEach

        if (type.rangedAttacks.isEmpty()) {
            return@forEach
        }

        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        meleeAttackMap[equipment] = resolveRangedAttacks(state, effects, type.rangedAttacks)
    }

    return meleeAttackMap
}
