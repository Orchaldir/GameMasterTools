package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.Statblock
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.rpg.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.rpg.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.UseStatblockOfTemplate
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun State.getStatblocksWith(statistic: StatisticId): List<Pair<Id<*>, Int>> {
    val statblocks = mutableListOf<Pair<Id<*>, Int>>()

    getCharacterTemplateStorage().getAll()
        .forEach { template ->
            addStatblock(statblocks, statistic, template.statblock, template.id)
        }

    getCharacterStorage().getAll()
        .forEach { character ->
            when (character.statblock) {
                UndefinedCharacterStatblock -> doNothing()
                is UniqueCharacterStatblock -> addStatblock(
                    statblocks,
                    statistic,
                    character.statblock.statblock,
                    character.id
                )

                is UseStatblockOfTemplate -> {
                    val template = getCharacterTemplateStorage().getOrThrow(character.statblock.template)

                    addStatblock(statblocks, statistic, template.statblock, character.id)
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
