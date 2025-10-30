package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AttackEffectType {
    Damage,
    Undefined,
}

@Serializable
sealed class AttackEffect {

    fun getType() = when (this) {
        is Damage -> AttackEffectType.Damage
        is UndefinedAttackEffect -> AttackEffectType.Undefined
    }

    fun contains(statistic: StatisticId)  = when (this) {
        is Damage -> amount.contains(statistic)
        is UndefinedAttackEffect -> false
    }
}

@Serializable
@SerialName("Damage")
data class Damage(
    val amount: DamageAmount,
    val damageType: DamageTypeId,
) : AttackEffect()

@Serializable
@SerialName("Undefined")
data object UndefinedAttackEffect : AttackEffect()
