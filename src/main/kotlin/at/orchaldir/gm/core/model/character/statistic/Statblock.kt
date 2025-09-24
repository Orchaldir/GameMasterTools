package at.orchaldir.gm.core.model.character.statistic

import at.orchaldir.gm.core.model.State
import kotlinx.serialization.Serializable

@Serializable
data class Statblock(
    val statistics: Map<StatisticId, Int> = emptyMap(),
) {
    fun resolve(state: State, statistics: List<Statistic>) = statistics.map { statistic ->
        Pair(statistic, resolve(state, statistic))
    }

    fun resolve(state: State, statistic: StatisticId) =
        resolve(state, state.getStatisticStorage().getOrThrow(statistic))

    fun resolve(state: State, statistic: Statistic): Int {
        return when (statistic.data) {
            is Attribute -> {
                val base = resolve(state, statistic.data.base)
                val offset = statistics[statistic.id] ?: 0

                base + offset
            }

            is Skill -> {
                val base = resolve(state, statistic.data.base)
                val offset = statistics[statistic.id] ?: 0

                base + offset
            }
        }
    }

    private fun resolve(state: State, base: BaseValue): Int {
        return when (base) {
            is BasedOnStatistic -> {
                val resolvedBase = resolve(state, base.statistic)
                resolvedBase + base.offset
            }
            is FixedNumber -> base.default
            is DivisionOfValues -> resolve(state, base.dividend) / resolve(state, base.divisor)
            is ProductOfValues -> base.values
                .map { resolve(state, it) }
                .reduce { product, value -> product * value }
            is SumOfValues -> base.values.sumOf { resolve(state, it) }
        }
    }

}
