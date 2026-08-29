package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.util.quantity.Quantity
import at.orchaldir.gm.core.model.util.quantity.StandardDice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val modifier: Quantity = StandardDice(0, 0),
) : DamageAmount()

@Serializable
@SerialName("SimpleRandom")
data class SimpleRandomDamage(
    val amount: Quantity = StandardDice(1, 0),
) : DamageAmount()
