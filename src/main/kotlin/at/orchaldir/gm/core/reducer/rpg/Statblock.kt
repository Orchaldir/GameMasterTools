package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate

fun validateStatblock(
    state: State,
    statblock: Statblock,
) {
    state.getCharacterTraitStorage().require(statblock.traits)
    state.getStatisticStorage().require(statblock.statistics.keys)
}

fun validateStatblockUpdate(
    state: State,
    statblockUpdate: StatblockUpdate,
) {
    state.getCharacterTraitStorage().require(statblockUpdate.addedTraits)
    state.getCharacterTraitStorage().require(statblockUpdate.removedTraits)
    state.getStatisticStorage().require(statblockUpdate.statistics.keys)
}
