package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticDataType
import at.orchaldir.gm.utils.doNothing

fun validateMeleeWeapon(
    state: State,
    weapon: MeleeWeapon,
) {
    state.getMeleeWeaponTypeStorage().requireOptional(weapon.type)
}
