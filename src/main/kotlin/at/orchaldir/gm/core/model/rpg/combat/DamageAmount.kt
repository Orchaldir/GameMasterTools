package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class DamageAmountType {
    ModifiedBase,
    SimpleRandom,
}

@Serializable
sealed class DamageAmount {

    fun getType() = when (this) {
        is SimpleRandomDamage -> DamageAmountType.SimpleRandom
        is ModifiedBaseDamage -> DamageAmountType.ModifiedBase
    }

    fun contains(statistic: StatisticId) = when (this) {
        is SimpleRandomDamage -> false
        is ModifiedBaseDamage -> base == statistic
    }

    fun apply(effect: ModifiedDamage) = when (this) {
        is ModifiedBaseDamage -> copy(modifier = modifier + effect.amount.modifier) // TODO: handle dice
        is SimpleRandomDamage -> SimpleRandomDamage(amount + effect.amount)
    }
}

@Serializable
@SerialName("ModifiedBase")
data class ModifiedBaseDamage(
    val base: StatisticId,
    val modifier: Int = 0,
) : DamageAmount()

@Serializable
@SerialName("SimpleRandom")
data class SimpleRandomDamage(
    val amount: SimpleModifiedDice = SimpleModifiedDice(1, 0),
) : DamageAmount()
