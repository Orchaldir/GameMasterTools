package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun State.getStatblock(id: CharacterTemplateId): Statblock {
    val template = getCharacterTemplateStorage().getOrThrow(id)
    return getStatblock(template.statblock)
}

fun State.getStatblockOrNull(lookup: StatblockLookup): Statblock? = when (lookup) {
    is UniqueStatblock -> lookup.statblock
    is UseStatblockOfTemplate -> getStatblock(lookup.template)
    is ModifyStatblockOfTemplate -> {
        val statblock = getStatblock(lookup.template)

        lookup.update.resolve(statblock)
    }

    UndefinedStatblockLookup -> null
}

fun State.getStatblock(lookup: StatblockLookup): Statblock = getStatblockOrNull(lookup) ?: Statblock()

fun State.getStatblocksWith(statistic: StatisticId): List<Pair<Id<*>, Int>> {
    val statblocks = mutableListOf<Pair<Id<*>, Int>>()

    getCharacterTemplateStorage().getAll()
        .forEach { template ->
            addStatblock(statblocks, statistic, getStatblock(template.statblock), template.id)
        }

    getCharacterStorage().getAll()
        .forEach { character ->
            when (character.statblock) {
                UndefinedStatblockLookup -> doNothing()
                is UniqueStatblock -> addStatblock(
                    statblocks,
                    statistic,
                    character.statblock.statblock,
                    character.id
                )

                is UseStatblockOfTemplate -> {
                    val statblock = getStatblock(character.statblock.template)

                    addStatblock(statblocks, statistic, statblock, character.id)
                }

                is ModifyStatblockOfTemplate -> {
                    val statblock = getStatblock(character.statblock.template)
                    val resolvedStatblock = character.statblock.update.resolve(statblock)

                    addStatblock(statblocks, statistic, resolvedStatblock, character.id)
                }
            }
        }

    return statblocks
}

private fun State.addStatblock(
    statblocks: MutableList<Pair<Id<*>, Int>>,
    statistic: StatisticId,
    statblock: Statblock,
    id: Id<*>,
) {
    statblock.resolve(this, statistic)?.let { value ->
        statblocks.add(Pair(id, value))
    }
}
