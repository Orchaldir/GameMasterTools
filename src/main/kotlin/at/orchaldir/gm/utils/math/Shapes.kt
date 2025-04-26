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

fun createRegularPolygon(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation) =
    Polygon2d(createRegularPolygonPoints(center, radius, sides, firstCorner))

fun createRoundedRegularPolygon(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation) =
    Polygon2d(
        subdividePolygon(
            createRegularPolygonPoints(center, radius, sides, firstCorner),
            ::halfSegment,
        )
    )

fun createRegularPolygonPoints(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation): List<Point2d> {
    require(sides >= 3) { "A regular polygon needs at least 3 sides!" }

    val step = FULL_CIRCLE / sides
    var orientation = firstCorner

    return (0..<sides).map {
        val point = center.createPolar(radius, orientation)

        orientation += step

        point
    }
}

fun createSquare(center: Point2d, radius: Distance) =
    Polygon2d(createSquarePoints(center, radius))

fun createRoundedSquare(center: Point2d, radius: Distance) =
    Polygon2d(subdividePolygon(createSquarePoints(center, radius), ::halfSegment))

fun createSquarePoints(center: Point2d, radius: Distance) = AABB.fromRadius(center, radius).getCorners()

fun createTriangle(center: Point2d, radius: Distance, firstCorner: Orientation) =
    Polygon2d(createTrianglePoints(center, radius, firstCorner))

fun createRoundedTriangle(center: Point2d, radius: Distance, firstCorner: Orientation) =
    Polygon2d(subdividePolygon(createTrianglePoints(center, radius, firstCorner), ::halfSegment))

fun createTrianglePoints(center: Point2d, radius: Distance, firstCorner: Orientation) = listOf(
    center.createPolar(radius, firstCorner),
    center.createPolar(radius, firstCorner + ONE_THIRD_CIRCLE),
    center.createPolar(radius, firstCorner + TWO_THIRD_CIRCLE),
)