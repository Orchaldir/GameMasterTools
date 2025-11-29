package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.reducer.rpg.validateIsInside
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

    fun validate(state: State) {
        val rpg = state.data.rpg

        when (this) {
            is ModifyDamage -> amount.validate("ModifyDamage", rpg.damageModifier)
            is ModifyDamageResistance -> validateIsInside(
                amount,
                "Damage Resistance Modifier",
                1,
                rpg.maxDamageResistance
            )

            is ModifyDefenseBonus -> validateIsInside(amount, "Defense Bonus Modifier", 1, rpg.maxDefenseBonus)
        }
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
