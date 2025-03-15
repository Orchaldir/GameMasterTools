package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.SiUnit

fun <T : SiUnit<T>> checkIsInside(
    distribution: Distribution<T>,
    value: T,
    message: () -> String,
) {
    require(distribution.isInside(value)) { message() }
}