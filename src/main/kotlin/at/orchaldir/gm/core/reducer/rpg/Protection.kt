package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing

fun validateProtection(
    state: State,
    protection: Protection,
) {
    val rpg = state.data.rpg

    when (protection) {
        is DamageResistance -> {
            require(protection.amount > 0) { "Damage Resistance needs to be greater 0!" }
            require(protection.amount <= rpg.maxDamageResistance) { "Damage Resistance is too high!" }
        }
        is DamageResistances -> {
            require(protection.amount >= 0) { "Damage Resistance needs to be >= 0!" }
            require(protection.amount <= rpg.maxDamageResistance) { "Damage Resistance is too high!" }

            protection.damageTypes.forEach { (type, dr) ->
                state.getDamageTypeStorage().require(type)
                require(dr >= 0) { "Damage Resistance for ${type.print()} needs to be >= 0!" }
                require(dr <= rpg.maxDamageResistance) { "Damage Resistance for ${type.print()} is too high!" }
            }
        }

        is DefenseBonus -> {
            require(protection.bonus > 0) { "Defense Bonus needs to be greater 0!" }
            require(protection.bonus <= rpg.maxDefenseBonus) { "Defense Bonus is too high!" }
        }
        UndefinedProtection -> doNothing()
    }
}
