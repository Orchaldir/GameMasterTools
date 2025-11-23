package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.math.CircularArrangement
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.FULL_CIRCLE
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION

fun <T> visualizeCircularArrangement(
    arrangement: CircularArrangement<T>,
    center: Point2d,
    radius: Distance,
    renderItem: (Int, Point2d, Orientation) -> Unit
) {
    val step = FULL_CIRCLE / arrangement.number
    var orientation = ZERO_ORIENTATION
    val distance = radius * arrangement.radius

    (0..<arrangement.number).forEach { i ->
        val position = center.createPolar(distance, orientation)

        renderItem(i, position, orientation)

        orientation += step
    }
}