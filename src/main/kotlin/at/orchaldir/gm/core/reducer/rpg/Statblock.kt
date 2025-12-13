package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.utils.doNothing

fun validateStatblockLookup(
    state: State,
    statblock: StatblockLookup,
) {
    when (statblock) {
        UndefinedStatblockLookup -> doNothing()
        is UniqueStatblock -> validateStatblock(state, statblock.statblock)
        is UseStatblockOfTemplate -> state.getCharacterTemplateStorage().require(statblock.template)
        is ModifyStatblockOfTemplate -> {
            val template = state.getCharacterTemplateStorage().getOrThrow(statblock.template)
            validateStatblockUpdate(state, template.statblock, statblock.update)
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
    statblock: Statblock,
    update: StatblockUpdate,
) {
    state.getCharacterTraitStorage().require(update.addedTraits)
    state.getCharacterTraitStorage().require(update.removedTraits)
    state.getStatisticStorage().require(update.statistics.keys)

    update.addedTraits.forEach {
        require(!update.removedTraits.contains(it)) {
            "Cannot add & remove ${it.print()}!"
        }
    }

    update.removedTraits.forEach {
        require(statblock.traits.contains(it)) {
            "Cannot remove ${it.print()}, because it is not in the statblock!"
        }
    }
}
