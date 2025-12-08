package at.orchaldir.gm.core.model.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import kotlinx.serialization.Serializable

@Serializable
data class StatblockUpdate(
    val statistics: Map<StatisticId, Int> = emptyMap(),
    val addedTraits: Set<CharacterTraitId> = emptySet(),
    val removedTraits: Set<CharacterTraitId> = emptySet(),
) {

    fun calculateCost(state: State) = calculateStatisticCost(state, statistics) + calculateTraitCost(state, addedTraits) - calculateTraitCost(state, removedTraits)

    fun resolve(statblock: Statblock): Statblock {
        val newStatistics = statblock.statistics.toMutableMap()

        statistics.forEach { (id, modifier) ->
            newStatistics[id] = newStatistics.getOrDefault(id, 0) + modifier
        }

        return Statblock(
            newStatistics,
            statblock.traits - removedTraits + addedTraits,
        )
    }

}