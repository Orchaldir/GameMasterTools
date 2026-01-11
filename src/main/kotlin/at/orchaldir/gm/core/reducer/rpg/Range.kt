package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing

fun validateRange(
    state: State,
    range: Range,
) {
    when (range) {
        is FixedHalfAndMaxRange -> validateRange(range.half, range.max)
        is MusclePoweredHalfAndMaxRange -> TODO()
        is StatisticBasedHalfAndMaxRange -> TODO()
        UndefinedRange -> doNothing()
    }
}

private fun validateRange(
    half: Int,
    max: Int,
) {
    require(half > 0) { "Half range must be > 0!" }
    require(max > half) { "Max range must be > half range!" }
}
