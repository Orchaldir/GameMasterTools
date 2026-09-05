package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.equipment.ShieldStats

fun validateShieldStats(
    state: State,
    stats: ShieldStats,
) {
    state.getEquipmentModifierStorage().require(stats.modifiers)
    state.getShieldTypeStorage().requireOptional(stats.type)
}
