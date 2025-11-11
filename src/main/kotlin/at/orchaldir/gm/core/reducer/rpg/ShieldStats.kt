package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ShieldStats

fun validateShieldStats(
    state: State,
    stats: ShieldStats,
) {
    state.getArmorModifierStorage().require(stats.modifiers)
    state.getShieldTypeStorage().requireOptional(stats.type)
}
