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
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects

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