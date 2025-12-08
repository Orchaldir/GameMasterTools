package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.Statblock

fun validateStatblock(
    state: State,
    statblock: Statblock,
) {
    state.getCharacterTraitStorage().require(statblock.traits)
    state.getStatisticStorage().require(statblock.statistics.keys)
}
