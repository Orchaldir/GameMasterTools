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
        is DamageResistance -> validateIsInside(protection.amount, "Damage Resistance", 1, rpg.maxDamageResistance)
        is DamageResistances -> {
            validateIsInside(protection.amount, "Damage Resistance", 0, rpg.maxDamageResistance)

            protection.damageTypes.forEach { (type, dr) ->
                state.getDamageTypeStorage().require(type)
                validateIsInside(dr, "Damage Resistance for ${type.print()}", 0, rpg.maxDamageResistance)
            }
        }

        is DefenseBonus -> validateIsInside(protection.bonus, "Defense Bonus", 1, rpg.maxDefenseBonus)
        UndefinedProtection -> doNothing()
    }
}
