package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.CharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.ModifyStatblockOfTemplate
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.utils.doNothing

fun validateCharacterStatblock(
    state: State,
    statblock: CharacterStatblock,
) {
    when (statblock) {
        UndefinedCharacterStatblock -> doNothing()
        is UniqueCharacterStatblock -> validateStatblock(state, statblock.statblock)
        is UseStatblockOfTemplate -> state.getCharacterTemplateStorage().require(statblock.template)
        is ModifyStatblockOfTemplate -> {
            state.getCharacterTemplateStorage().require(statblock.template)
            validateStatblockUpdate(state, statblock.update)
        }
    }
}

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
