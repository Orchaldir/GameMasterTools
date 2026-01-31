package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statblock.*

// resolve ranged attack with statblock

fun resolveRangedAttackMap(
    state: State,
    base: Statblock,
    lookup: StatblockLookup,
    attackMap: Map<Equipment, List<RangedAttack>>,
) = when (lookup) {
    UndefinedStatblockLookup -> attackMap
    is UniqueStatblock -> {
        val statblock = lookup.statblock.applyTo(base)
        resolveRangedAttackMap(state, statblock, attackMap)
    }

    is UseStatblockOfTemplate -> {
        val statblock = state.getStatblock(base, lookup.template)

        resolveRangedAttackMap(state, statblock, attackMap)
    }

    is ModifyStatblockOfTemplate -> {
        val statblock = state.getStatblock(base, lookup.template)
        val resolvedStatblock = lookup.update.applyTo(statblock)

        resolveRangedAttackMap(state, resolvedStatblock, attackMap)
    }
}

fun resolveRangedAttackMap(
    state: State,
    statblock: Statblock,
    attackMap: Map<Equipment, List<RangedAttack>>,
) = attackMap.mapValues { (_, attacks) ->
    attacks.map { attack ->
        resolveRangedAttack(state, statblock, attack)
    }
}

fun resolveRangedAttack(
    state: State,
    statblock: Statblock,
    attack: RangedAttack,
) = attack.copy(
    effect = resolveAttackEffect(state, statblock, attack.effect),
    range = resolveRange(state, statblock, attack.range),
)

// resolve ranged attack with modifier effects

fun resolveRangedAttacks(effects: List<EquipmentModifierEffect>, attacks: List<RangedAttack>) = attacks.map { attack ->
    resolveRangedAttack(effects, attack)
}

fun resolveRangedAttack(
    effects: List<EquipmentModifierEffect>,
    attack: RangedAttack,
): RangedAttack {
    var resolved = attack

    effects.forEach { effect ->
        resolved = resolveRangedAttack(effect, resolved)
    }

    return resolved
}

fun resolveRangedAttack(
    effect: EquipmentModifierEffect,
    attack: RangedAttack,
) = when (effect) {
    is ModifyDamageResistance, is ModifyDefenseBonus, is ModifyParrying -> attack
    is ModifyDamage -> attack.copy(effect = resolveAttackEffect(effect, attack.effect))
    is ModifyRange -> attack.copy(range = resolveRange(effect, attack.range))
    is ModifySkill -> attack.copy(skill = resolveUsedSkill(effect, attack.skill))
}
