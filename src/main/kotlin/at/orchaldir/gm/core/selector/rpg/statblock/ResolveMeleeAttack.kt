package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.equipment.*
import at.orchaldir.gm.core.model.rpg.statblock.Statblock

// resolve melee attack with statblock

fun resolveMeleeAttackMap(
    state: State,
    statblock: Statblock,
    attackMap: Map<Equipment, List<MeleeAttack>>,
) = attackMap.mapValues { (_, attacks) ->
    attacks.map { attack ->
        resolveMeleeAttack(state, statblock, attack)
    }
}

fun resolveMeleeAttack(
    state: State,
    statblock: Statblock,
    attack: MeleeAttack,
) = attack.copy(effect = resolveAttackEffect(state, statblock, attack.effect))

// resolve melee attack with modifier effects

fun resolveMeleeAttacks(
    state: State,
    effects: List<EquipmentModifierEffect>,
    attacks: List<MeleeAttack>,
) = attacks.map { attack ->
    resolveMeleeAttack(state, effects, attack)
}

fun resolveMeleeAttack(
    state: State,
    effects: List<EquipmentModifierEffect>,
    attack: MeleeAttack,
): MeleeAttack {
    var resolved = attack

    effects.forEach { effect ->
        resolved = resolveMeleeAttack(state, effect, resolved)
    }

    return resolved
}

fun resolveMeleeAttack(
    state: State,
    effect: EquipmentModifierEffect,
    attack: MeleeAttack,
) = when (effect) {
    is ModifyDamageResistance, is ModifyDefenseBonus, is ModifyRange -> attack
    is ModifyDamage -> attack.copy(effect = resolveAttackEffect(state, effect, attack.effect))
    is ModifyParrying -> attack.copy(parrying = resolveParrying(effect, attack.parrying))
    is ModifySkill -> attack.copy(skill = resolveUsedSkill(effect, attack.skill))
}
