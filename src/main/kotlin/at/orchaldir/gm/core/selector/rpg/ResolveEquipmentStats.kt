package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.Statblock
import at.orchaldir.gm.core.model.rpg.combat.AttackEffect
import at.orchaldir.gm.core.model.rpg.combat.Damage
import at.orchaldir.gm.core.model.rpg.combat.DamageAmount
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.SimpleRandomDamage
import at.orchaldir.gm.core.model.rpg.combat.StatisticBasedDamage
import at.orchaldir.gm.core.model.rpg.combat.UndefinedAttackEffect

// resolve melee attack with statblock

fun resolveMeleeAttack(
    state: State,
    statblock: Statblock,
    attack: MeleeAttack,
) = attack.copy(effect = resolveAttackEffect(state, statblock, attack.effect))

fun resolveAttackEffect(
    state: State,
    statblock: Statblock,
    effect: AttackEffect,
) = when (effect) {
    is Damage -> effect.copy(amount = resolveDamageAmount(state, statblock, effect.amount))
    UndefinedAttackEffect -> effect
}

fun resolveDamageAmount(
    state: State,
    statblock: Statblock,
    amount: DamageAmount,
) = when (amount) {
    is SimpleRandomDamage -> amount
    is StatisticBasedDamage -> {
        val statistic = state.getStatisticStorage().getOrThrow(amount.base)
        val value = statblock.resolve(state, statistic) ?: error("Failed to resolve ${amount.base.print()} with statblock!")

        SimpleRandomDamage(statistic.data.resolveDamage(value))
    }
}