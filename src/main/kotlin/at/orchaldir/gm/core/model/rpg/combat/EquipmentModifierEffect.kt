package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquipmentModifierEffectType {
    Undefined,
    ModifiedDamage,
}

@Serializable
sealed class EquipmentModifierEffect {

    fun getType() = when (this) {
        is ModifiedDamage -> EquipmentModifierEffectType.ModifiedDamage
        is UndefinedEquipmentModifierEffect -> EquipmentModifierEffectType.Undefined
    }
}

@Serializable
@SerialName("ModifiedDamage")
data class ModifiedDamage(
    val amount: SimpleModifiedDice,
) : EquipmentModifierEffect()

@Serializable
@SerialName("Undefined")
data object UndefinedEquipmentModifierEffect : EquipmentModifierEffect()
