package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance

fun createCross(center: Point2d, height: Distance): Polygon2d {
    val aabb = AABB.fromRadii(center, height / 4.0f, height / 2.0f)
    val size = fromPercentage(15)
    val halfSize = size / 2.0f
    val y = fromPercentage(33)

    return Polygon2dBuilder()
        .addMirroredPoints(aabb, size, START)
        .addMirroredPoints(aabb, size, y - halfSize)
        .addMirroredPoints(aabb, FULL, y - halfSize)
        .addMirroredPoints(aabb, FULL, y + halfSize)
        .addMirroredPoints(aabb, size, y + halfSize)
        .addMirroredPoints(aabb, size, END)
        .build()
}
