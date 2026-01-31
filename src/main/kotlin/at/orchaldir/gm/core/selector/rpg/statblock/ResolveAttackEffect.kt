package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statblock.Statblock

// resolve attack effect with statblock

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
        val value =
            statblock.resolve(state, statistic) ?: error("Failed to resolve ${amount.base.print()} with statblock!")

        SimpleRandomDamage(statistic.data.resolveDamage(value) + amount.modifier)
    }
}

// resolve attack effect with modifier effects

fun resolveAttackEffect(
    modifier: ModifyDamage,
    attackEffect: AttackEffect,
) = when (attackEffect) {
    is Damage -> attackEffect.copy(amount = attackEffect.amount.apply(modifier))
    UndefinedAttackEffect -> attackEffect
}
