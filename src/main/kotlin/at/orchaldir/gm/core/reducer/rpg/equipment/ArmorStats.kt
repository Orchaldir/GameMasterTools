package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.equipment.ArmorStats

fun validateArmorStats(
    state: State,
    stats: ArmorStats,
) {
    state.getEquipmentModifierStorage().require(stats.modifiers)
    state.getEquipmentTypeStorage().requireOptional(stats.type)
}
