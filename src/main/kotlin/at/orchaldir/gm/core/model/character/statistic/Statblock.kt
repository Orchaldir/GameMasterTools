package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.Serializable
import at.orchaldir.gm.core.model.State

@Serializable
data class Statblock(
    val statistics: Map<StatisticId, Int> = emptyMap(),
) {
    fun resolve(state: State, statistics: List<Statistic>) = statistics.mapNotNull { statistic ->
        resolve(state, statistic)?.let { Pair(statistic, it) }
    }

    fun resolve(state: State, statistic: StatisticId) =
        resolve(state, state.getStatisticStorage().getOrThrow(statistic))

    fun resolve(state: State, statistic: Statistic): Int? {
        return when (statistic.data) {
            is Attribute -> {
                val base = resolve(state, statistic.data.base) ?: return null
                val offset = statistics[statistic.id] ?: 0

                base + offset
            }
            is Skill -> {
                val base = resolve(state, statistic.data.base) ?: return null
                val offset = statistics[statistic.id] ?: return null

                base + offset
            }
        }
    }

    private fun resolve(state: State, base: BaseValue): Int? {
        return when (base) {
            is BasedOnStatistic -> {
                val resolvedBase = resolve(state, base.statistic) ?: return null
                resolvedBase + base.offset
            }
            is FixedNumber -> base.default
        }
    }

}
