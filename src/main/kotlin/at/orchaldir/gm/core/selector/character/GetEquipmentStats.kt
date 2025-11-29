package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Equipped
import at.orchaldir.gm.core.model.character.EquippedEquipment
import at.orchaldir.gm.core.model.character.EquippedUniform
import at.orchaldir.gm.core.model.character.UndefinedEquipped
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.Protection
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.resolveProtection

// get armors

fun getArmors(state: State, equipped: Equipped): Map<Equipment, Protection> {
    return when (equipped) {
        UndefinedEquipped -> emptyMap()
        is EquippedEquipment -> getArmors(state, equipped.map)
        is EquippedUniform -> getArmors(state, equipped.uniform)
    }
}

fun getArmors(state: State, id: UniformId): Map<Equipment, Protection> {
    val uniform = state.getUniformStorage().getOrThrow(id)

    return getArmors(state, uniform.equipmentMap)
}

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

fun getMeleeAttacks(state: State, equipped: Equipped): Map<Equipment, List<MeleeAttack>> {
    return when (equipped) {
        UndefinedEquipped -> emptyMap()
        is EquippedEquipment -> getMeleeAttacks(state, equipped.map)
        is EquippedUniform -> getMeleeAttacks(state, equipped.uniform)
    }
}

fun getMeleeAttacks(state: State, id: UniformId): Map<Equipment, List<MeleeAttack>> {
    val uniform = state.getUniformStorage().getOrThrow(id)

    return getMeleeAttacks(state, uniform.equipmentMap)
}

fun getMeleeAttacks(state: State, map: EquipmentIdMap): Map<Equipment, List<MeleeAttack>> {
    val meleeAttackMap = mutableMapOf<Equipment, List<MeleeAttack>>()

    map.getAllEquipment().forEach { (id, _) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val stats = equipment.data.getMeleeWeaponStats() ?: return@forEach
        val type = state.getMeleeWeaponTypeStorage().getOptional(stats.type) ?: return@forEach
        val effects = state.getEquipmentModifierEffects(stats.modifiers)

        meleeAttackMap[equipment] = type.apply(effects)
    }

    return meleeAttackMap
}