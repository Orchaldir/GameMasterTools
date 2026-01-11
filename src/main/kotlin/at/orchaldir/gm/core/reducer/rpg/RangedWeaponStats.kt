package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponStats

fun validateRangedWeaponStats(
    state: State,
    stats: RangedWeaponStats,
) {
    state.getEquipmentModifierStorage().require(stats.modifiers)
    state.getRangedWeaponTypeStorage().requireOptional(stats.type)
}
