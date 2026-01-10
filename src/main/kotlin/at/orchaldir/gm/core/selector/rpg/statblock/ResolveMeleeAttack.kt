package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statblock.*

// resolve melee attack with statblock

fun resolveMeleeAttackMap(
    state: State,
    base: Statblock,
    lookup: StatblockLookup,
    attackMap: Map<Equipment, List<MeleeAttack>>,
) = when (lookup) {
    UndefinedStatblockLookup -> attackMap
    is UniqueStatblock -> {
        val statblock = lookup.statblock.applyTo(base)
        resolveMeleeAttackMap(state, statblock, attackMap)
    }

    is UseStatblockOfTemplate -> {
        val statblock = state.getStatblock(base, lookup.template)

        resolveMeleeAttackMap(state, statblock, attackMap)
    }

    is ModifyStatblockOfTemplate -> {
        val statblock = state.getStatblock(base, lookup.template)
        val resolvedStatblock = lookup.update.applyTo(statblock)

        resolveMeleeAttackMap(state, resolvedStatblock, attackMap)
    }
}

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

fun resolveMeleeAttacks(effects: List<EquipmentModifierEffect>, attacks: List<MeleeAttack>) = attacks.map { attack ->
    resolveMeleeAttack(effects, attack)
}

fun resolveMeleeAttack(
    effects: List<EquipmentModifierEffect>,
    attack: MeleeAttack,
): MeleeAttack {
    var resolved = attack

    effects.forEach { effect ->
        resolved = resolveMeleeAttack(effect, resolved)
    }

    return resolved
}

fun resolveMeleeAttack(
    effect: EquipmentModifierEffect,
    attack: MeleeAttack,
) = when (effect) {
    is ModifyDamageResistance, is ModifyDefenseBonus -> attack
    is ModifyDamage -> attack.copy(effect = resolveAttackEffect(effect, attack.effect))
}
