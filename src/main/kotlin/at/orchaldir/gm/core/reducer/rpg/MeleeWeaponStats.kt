package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats

fun validateMeleeWeaponStats(
    state: State,
    weapon: MeleeWeaponStats,
) {
    state.getMeleeWeaponModifierStorage().require(weapon.modifiers)
    state.getMeleeWeaponTypeStorage().requireOptional(weapon.type)
}
