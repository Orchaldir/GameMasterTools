package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.dice.RandomNumber
import at.orchaldir.gm.core.model.rpg.dice.StandardDice
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import at.orchaldir.gm.core.model.State

enum class DamageAmountType {
    StatisticBased,
    SimpleRandom,
}

@Serializable
sealed class DamageAmount {

    fun getType() = when (this) {
        is SimpleRandomDamage -> DamageAmountType.SimpleRandom
        is StatisticBasedDamage -> DamageAmountType.StatisticBased
    }

    fun contains(statistic: StatisticId) = when (this) {
        is SimpleRandomDamage -> false
        is StatisticBasedDamage -> base == statistic
    }

    fun apply(state: State, effect: ModifyDamage) = when (this) {
        is StatisticBasedDamage -> copy(modifier = modifier.add(state, effect.amount))
        is SimpleRandomDamage -> SimpleRandomDamage(amount.add(state, effect.amount))
    }
}

@Serializable
@SerialName("StatisticBased")
data class StatisticBasedDamage(
    val base: StatisticId,
    val modifier: RandomNumber = StandardDice(0, 0),
) : DamageAmount()

@Serializable
@SerialName("SimpleRandom")
data class SimpleRandomDamage(
    val amount: RandomNumber = StandardDice(1, 0),
) : DamageAmount()
