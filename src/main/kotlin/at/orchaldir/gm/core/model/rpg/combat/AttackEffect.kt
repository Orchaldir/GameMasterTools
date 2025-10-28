package at.orchaldir.gm.core.model.rpg.combat

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
