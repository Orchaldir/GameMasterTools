package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.FixedHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.MusclePoweredHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.Range
import at.orchaldir.gm.core.model.rpg.combat.StatisticBasedHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.utils.math.Factor

// resolve attack range with statblock

fun resolveRange(
    state: State,
    statblock: Statblock,
    range: Range,
) = when (range) {
    is MusclePoweredHalfAndMaxRange -> {
        val statistic = state.data.rpg.musclePoweredStatistic ?: error("MusclePoweredHalfAndMaxRange is unsupported!")

        resolve(state, statblock, statistic, range.half, range.max)
    }

    is StatisticBasedHalfAndMaxRange -> resolve(state, statblock, range.statistic, range.half, range.max)
    else -> range
}

private fun resolve(
    state: State,
    statblock: Statblock,
    statisticId: StatisticId,
    half: Factor,
    max: Factor,
): Range {
    val statistic = state.getStatisticStorage().getOrThrow(statisticId)
    val value = statblock.resolve(state, statistic) ?: error("Failed to resolve ${statisticId.print()} with statblock!")

    return FixedHalfAndMaxRange(
        half.apply(value),
        max.apply(value),
    )
}
