package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Distribution

fun checkIsInside(
    distribution: Distribution,
    distance: Distance,
    message: () -> String,
) {
    require(distribution.isInside(distance)) { message() }
}