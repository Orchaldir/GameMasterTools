package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing

fun validateProtection(
    state: State,
    protection: Protection,
) {
    when (protection) {
        is DamageResistance -> require(protection.amount > 0) { "Damage Resistance needs to be greater 0!" }
        is DamageResistances -> {
            require(protection.amount >= 0) { "Damage Resistance needs to be >= 0!" }

            protection.damageTypes.forEach { (type, dr) ->
                state.getDamageTypeStorage().require(type)
                require(dr >= 0) { "Damage Resistance for ${type.print()} needs to be >= 0!" }
            }
        }
        UndefinedProtection -> doNothing()
    }
}
