package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
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

    fun apply(effect: ModifyDamage) = when (this) {
        is StatisticBasedDamage -> copy(modifier = modifier + effect.amount)
        is SimpleRandomDamage -> SimpleRandomDamage(amount + effect.amount)
    }
}

@Serializable
@SerialName("StatisticBased")
data class StatisticBasedDamage(
    val base: StatisticId,
    val modifier: SimpleModifiedDice = SimpleModifiedDice(0, 0),
) : DamageAmount()

@Serializable
@SerialName("SimpleRandom")
data class SimpleRandomDamage(
    val amount: SimpleModifiedDice = SimpleModifiedDice(1, 0),
) : DamageAmount()
