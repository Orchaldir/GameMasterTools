package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statblock

fun validateStatblock(
    state: State,
    statblock: Statblock,
) {
    state.getStatisticStorage().require(statblock.statistics.keys)
}
