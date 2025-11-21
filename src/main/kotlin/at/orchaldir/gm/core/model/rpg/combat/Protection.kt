package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ProtectionType {
    DamageResistance,
    DamageResistances,
    DefenseBonus,
    Undefined,
}

@Serializable
sealed class Protection {

    fun getType() = when (this) {
        is DamageResistance -> ProtectionType.DamageResistance
        is DamageResistances -> ProtectionType.DamageResistances
        is DefenseBonus -> ProtectionType.DefenseBonus
        is UndefinedProtection -> ProtectionType.Undefined
    }

    fun contains(type: DamageTypeId) = when (this) {
        is DamageResistance -> false
        is DamageResistances -> damageTypes.containsKey(type)
        is DefenseBonus -> false
        UndefinedProtection -> false
    }

    fun apply(effects: List<EquipmentModifierEffect>): Protection {
        var protection = this

        effects.forEach { effect ->
            protection = protection.apply(effect)
        }

        return protection
    }

    fun apply(effect: EquipmentModifierEffect) = when (effect) {
        is ModifyDamageResistance -> when (this) {
            is DamageResistance -> {
                DamageResistance(amount + effect.amount)
            }

            is DamageResistances -> {
                copy(amount = amount + effect.amount)
            }

            else -> {
                this
            }
        }
        is ModifyDefenseBonus -> if (this is DefenseBonus) {
            DefenseBonus(bonus + effect.amount)
        } else {
            this
        }
        is ModifyDamage -> this
    }
}

@Serializable
@SerialName("DamageResistance")
data class DamageResistance(
    val amount: Int = 1,
) : Protection()

@Serializable
@SerialName("DamageResistances")
data class DamageResistances(
    val amount: Int = 1,
    val damageTypes: Map<DamageTypeId, Int> = emptyMap(),
) : Protection()

@Serializable
@SerialName("DefenseBonus")
data class DefenseBonus(
    val bonus: Int = 1,
) : Protection()

@Serializable
@SerialName("Undefined")
data object UndefinedProtection : Protection()
