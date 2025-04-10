package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance

fun calculatePointsOnLine(line: Line2d, step: Distance): List<Point2d> {
    var last = line.points[0]
    val points = mutableListOf(last)
    var remaining = Distance.fromMeters(0)

    line.points.drop(1).forEach { next ->
        val diff = next.minus(last)
        val distance = diff.length()
        val normalizedDiff = diff.normalize()
        var current = remaining

        while (current <= distance) {
            val point = last + normalizedDiff * current

            points.add(point)

            current += step
        }

        last = next
        remaining = current - distance
    }

    points.add(line.points.last())

    return points
}
