package at.orchaldir.gm.core.model.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import kotlinx.serialization.Serializable

@Serializable
data class Statblock(
    val statistics: Map<StatisticId, Int> = emptyMap(),
    val traits: Set<CharacterTraitId> = emptySet(),
) {
    fun calculateCost(state: State) = calculateStatisticCost(state, statistics) + calculateTraitCost(state, traits)

    fun resolve(state: State, statistics: List<Statistic>) = statistics.mapNotNull { statistic ->
        resolve(state, statistic)?.let { Pair(statistic, it) }
    }

    fun resolve(state: State, statistic: StatisticId) =
        resolve(state, state.getStatisticStorage().getOrThrow(statistic))

    fun resolve(state: State, statistic: Statistic): Int? {
        return when (statistic.data) {
            is Attribute -> resolveAttribute(state, statistic.id, statistic.data.base)
            is BaseDamage -> resolveAttribute(state, statistic.id, statistic.data.base)
            is DerivedAttribute -> resolveAttribute(state, statistic.id, statistic.data.base)
            is Skill -> resolveSkill(state, statistic.id, statistic.data.base)
        }
    }

    private fun Statblock.resolveAttribute(
        state: State,
        statistic: StatisticId,
        value: BaseValue,
    ): Int? {
        val base = resolve(state, value) ?: return null
        val offset = statistics[statistic] ?: 0

        return base + offset
    }

    private fun Statblock.resolveSkill(
        state: State,
        statistic: StatisticId,
        value: BaseValue,
    ): Int? {
        val base = resolve(state, value) ?: return null
        val offset = statistics[statistic] ?: return null

        return base + offset
    }

    private fun resolve(state: State, base: BaseValue): Int? {
        return when (base) {
            is BasedOnStatistic -> {
                val resolvedBase = resolve(state, base.statistic) ?: return null
                resolvedBase + base.offset
            }

            is FixedNumber -> base.default
            is DivisionOfValues -> {
                val dividend = resolve(state, base.dividend)
                val divisor = resolve(state, base.divisor)

                return if (dividend != null && divisor != null) {
                    dividend / divisor
                } else {
                    null
                }
            }

            is ProductOfValues -> base.values
                .mapNotNull { resolve(state, it) }
                .reduceOrNull { product, value -> product * value }

            is SumOfValues -> base.values
                .mapNotNull { resolve(state, it) }
                .reduceOrNull { sum, value -> sum + value }
        }
    }

}

fun calculateStatisticCost(state: State, statistics: Map<StatisticId, Int>): Int {
    val storage = state.getStatisticStorage()

    return statistics
        .map { (id, level) ->
            storage
                .getOrThrow(id)
                .data
                .cost()
                .calculate(level)
        }.sum()
}

fun calculateTraitCost(state: State, traits: Set<CharacterTraitId>) = state.getCharacterTraitStorage()
    .getOrThrow(traits)
    .sumOf { it.cost }