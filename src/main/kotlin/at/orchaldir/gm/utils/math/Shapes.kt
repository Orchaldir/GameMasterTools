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

fun createTriangle(center: Point2d, radius: Distance, firstCorner: Orientation) =
    Polygon2d(createTrianglePoints(center, radius, firstCorner))

fun createRoundedTriangle(center: Point2d, radius: Distance, firstCorner: Orientation) =
    Polygon2d(subdividePoints(createTrianglePoints(center, radius, firstCorner), ::halfSegment))

fun createTrianglePoints(center: Point2d, radius: Distance, firstCorner: Orientation) = listOf(
    center.createPolar(radius, firstCorner),
    center.createPolar(radius, firstCorner + ONE_THIRD_CIRCLE),
    center.createPolar(radius, firstCorner + TWO_THIRD_CIRCLE),
)