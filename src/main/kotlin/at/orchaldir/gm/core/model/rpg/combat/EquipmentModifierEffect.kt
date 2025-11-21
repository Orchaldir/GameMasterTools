package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquipmentModifierEffectType {
    Damage,
    DamageResistance,
    DefenseBonus,
}

@Serializable
sealed class EquipmentModifierEffect {

    fun getType() = when (this) {
        is ModifyDamage -> EquipmentModifierEffectType.Damage
        is ModifyDamageResistance -> EquipmentModifierEffectType.DamageResistance
        is ModifyDefenseBonus -> EquipmentModifierEffectType.DefenseBonus
    }

    fun modify(attack: MeleeAttack) = when (this) {
        is ModifyDamage -> attack.copy(effect = attack.effect.apply(this))
        is ModifyDamageResistance, is ModifyDefenseBonus -> attack
    }
}

@Serializable
@SerialName("Damage")
data class ModifyDamage(
    val amount: SimpleModifiedDice,
) : EquipmentModifierEffect()

@Serializable
@SerialName("DamageResistance")
data class ModifyDamageResistance(
    val amount: Int,
) : EquipmentModifierEffect()

@Serializable
@SerialName("DefenseBonus")
data class ModifyDefenseBonus(
    val amount: Int,
) : EquipmentModifierEffect()
