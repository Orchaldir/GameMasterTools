package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.AttackEffect
import at.orchaldir.gm.core.model.rpg.combat.Damage
import at.orchaldir.gm.core.model.rpg.combat.DamageAmount
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.ModifiedBaseDamage
import at.orchaldir.gm.core.model.rpg.combat.Reach
import at.orchaldir.gm.core.model.rpg.combat.ReachRange
import at.orchaldir.gm.core.model.rpg.combat.SimpleRandomDamage
import at.orchaldir.gm.core.model.rpg.combat.SimpleReach
import at.orchaldir.gm.core.model.rpg.combat.UndefinedAttackEffect
import at.orchaldir.gm.core.model.rpg.combat.UndefinedReach
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.utils.doNothing

fun validateMeleeAttack(
    state: State,
    attack: MeleeAttack,
) {
    validateAttackEffect(state, attack.effect)
    validateReach(attack.reach)
}

fun validateAttackEffect(
    state: State,
    effect: AttackEffect,
) {
    when (effect) {
        is Damage -> {
            validateDamageAmount(state, effect.amount)
            state.getDamageTypeStorage().require(effect.damageType)
        }
        UndefinedAttackEffect -> doNothing()
    }
}

fun validateDamageAmount(
    state: State,
    amount: DamageAmount,
) {
    when (amount) {
        is ModifiedBaseDamage -> {
            val base = state.getStatisticStorage().getOrThrow(amount.base)
            require(base.data.getType() == StatisticDataType.Damage) {
                "Damage is based on ${amount.base.print()}, which is not a base damage!"
            }
        }
        is SimpleRandomDamage -> doNothing()
    }
}

fun validateReach(
    reach: Reach,
) {
    when (reach) {
        is ReachRange -> {
            require(reach.min >= 0) {"The minimum reach must be >= 0!" }
            require(reach.min < reach.max) {"The minimum reach must be < than its maximum!" }
        }
        is SimpleReach -> require(reach.distance >= 0) {"The simple reach reach must be >= 0!" }
        UndefinedReach -> doNothing()
    }
}
