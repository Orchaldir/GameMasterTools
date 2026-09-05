package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.equipment.MeleeWeaponStats

fun validateMeleeWeaponStats(
    state: State,
    stats: MeleeWeaponStats,
) {
    state.getEquipmentModifierStorage().require(stats.modifiers)
    state.getMeleeWeaponTypeStorage().requireOptional(stats.type)
}
