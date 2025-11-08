package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ArmorStats

fun validateArmorStats(
    state: State,
    weapon: ArmorStats,
) {
    state.getArmorModifierStorage().require(weapon.modifiers)
    state.getArmorTypeStorage().requireOptional(weapon.type)
}
