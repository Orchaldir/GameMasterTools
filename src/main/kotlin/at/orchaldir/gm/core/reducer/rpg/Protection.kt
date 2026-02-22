package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing

fun validateProtection(
    state: State,
    protection: Protection,
) {
    val data = state.config.rpg.equipment

    when (protection) {
        is DamageResistance -> validateIsInside(protection.amount, "Damage Resistance", 1, data.maxDamageResistance)
        is DamageResistances -> {
            validateIsInside(protection.amount, "Damage Resistance", 0, data.maxDamageResistance)

            protection.damageTypes.forEach { (type, dr) ->
                state.getDamageTypeStorage().require(type)
                validateIsInside(dr, "Damage Resistance for ${type.print()}", 0, data.maxDamageResistance)
            }
        }

        is DefenseBonus -> validateIsInside(protection.bonus, "Defense Bonus", 1, data.maxDefenseBonus)
        UndefinedProtection -> doNothing()
    }
}
