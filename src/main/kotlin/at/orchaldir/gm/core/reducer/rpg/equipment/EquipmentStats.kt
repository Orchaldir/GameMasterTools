package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats

fun validateEquipmentStats(
    state: State,
    stats: EquipmentStats,
) {
    state.getEquipmentModifierStorage().require(stats.modifiers)
    state.getEquipmentTypeStorage().requireOptional(stats.type)
}
