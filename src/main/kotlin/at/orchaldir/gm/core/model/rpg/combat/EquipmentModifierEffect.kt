package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.reducer.rpg.validateIsInside
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.validateFactor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_RANGE_MODIFIER = fromPercentage(-99)
val MAX_RANGE_MODIFIER = fromPercentage(10000)

enum class EquipmentModifierEffectType {
    Damage,
    DamageResistance,
    DefenseBonus,
    Parrying,
    Range,
    Skill,
}

@Serializable
sealed class EquipmentModifierEffect {

    fun getType() = when (this) {
        is ModifyDamage -> EquipmentModifierEffectType.Damage
        is ModifyDamageResistance -> EquipmentModifierEffectType.DamageResistance
        is ModifyDefenseBonus -> EquipmentModifierEffectType.DefenseBonus
        is ModifyParrying -> EquipmentModifierEffectType.Parrying
        is ModifyRange -> EquipmentModifierEffectType.Range
        is ModifySkill -> EquipmentModifierEffectType.Skill
    }

    fun validate(state: State) {
        val data = state.data.rpg.equipment

        when (this) {
            is ModifyDamage -> amount.validate("ModifyDamage", data.damageModifier)
            is ModifyDamageResistance -> validateIsInside(
                amount,
                "Damage Resistance Modifier",
                1,
                data.maxDamageResistance
            )

            is ModifyDefenseBonus -> validateIsInside(amount, "Defense Bonus Modifier", 1, data.maxDefenseBonus)
            is ModifyParrying -> validateIsInside(amount, "Parrying Modifier", data.parryingModifier)
            is ModifyRange -> validateFactor(factor, "range", MIN_RANGE_MODIFIER, MAX_RANGE_MODIFIER)
            is ModifySkill -> validateIsInside(amount, "Skill Modifier", data.skillModifier)
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

@Serializable
@SerialName("Parrying")
data class ModifyParrying(
    val amount: Int,
) : EquipmentModifierEffect()

@Serializable
@SerialName("Range")
data class ModifyRange(
    val factor: Factor,
) : EquipmentModifierEffect()

@Serializable
@SerialName("Skill")
data class ModifySkill(
    val amount: Int,
) : EquipmentModifierEffect()