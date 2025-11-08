package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeapon

fun validateMeleeWeapon(
    state: State,
    weapon: MeleeWeapon,
) {
    state.getMeleeWeaponModifierStorage().require(weapon.modifiers)
    state.getMeleeWeaponTypeStorage().requireOptional(weapon.type)
}
