package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statblock
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.model.character.statistic.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.character.statistic.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.character.statistic.UseStatblockOfTemplate
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun State.getStatblocksWith(statistic: StatisticId): List<Pair<Id<*>, Statblock>> {
    val statblocks = mutableListOf<Pair<Id<*>, Statblock>>()

    getCharacterTemplateStorage().getAll()
        .filter { it.statblock.statistics.containsKey(statistic) }
        .forEach { template ->
            statblocks.add(Pair(template.id, template.statblock))
        }

    getCharacterStorage().getAll()
        .forEach { character ->
            when (character.stateblock) {
                UndefinedCharacterStatblock -> doNothing()
                is UniqueCharacterStatblock -> statblocks.add(Pair(character.id, character.stateblock.statblock))
                is UseStatblockOfTemplate -> {
                    val template = getCharacterTemplateStorage().getOrThrow(character.stateblock.template)

                    statblocks.add(Pair(character.id, template.statblock))
                }
            }
        }

    return statblocks
}
