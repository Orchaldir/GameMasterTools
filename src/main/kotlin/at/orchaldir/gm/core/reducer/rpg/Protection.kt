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
        UndefinedProtection -> doNothing()
    }
}
