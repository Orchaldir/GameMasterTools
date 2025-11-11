package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats

fun validateMeleeWeaponStats(
    state: State,
    stats: MeleeWeaponStats,
) {
    state.getMeleeWeaponModifierStorage().require(stats.modifiers)
    state.getMeleeWeaponTypeStorage().requireOptional(stats.type)
}
