package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import at.orchaldir.gm.utils.doNothing

fun validateStatblockLookup(
    state: State,
    race: RaceId,
    lookup: StatblockLookup,
) = validateStatblockLookup(
    state,
    state.getRaceStorage().getOrThrow(race).lifeStages.statblock(),
    lookup,
)

fun validateStatblockLookup(
    state: State,
    statblock: Statblock,
    lookup: StatblockLookup,
) {
    when (lookup) {
        UndefinedStatblockLookup -> doNothing()
        is UniqueStatblock -> validateStatblockUpdate(state, statblock, lookup.statblock)
        is UseStatblockOfTemplate -> state.getCharacterTemplateStorage().require(lookup.template)
        is ModifyStatblockOfTemplate -> {
            val templateStatblock = state.getStatblock(statblock, lookup.template)
            validateStatblockUpdate(state, templateStatblock, lookup.update)
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

        require(!statblock.traits.contains(it)) {
            "Cannot add ${it.print()} again!"
        }
    }

    update.removedTraits.forEach {
        require(statblock.traits.contains(it)) {
            "Cannot remove ${it.print()}, because it is not in the statblock!"
        }
    }
}
