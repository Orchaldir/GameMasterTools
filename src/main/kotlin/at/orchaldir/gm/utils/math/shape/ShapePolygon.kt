package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.createSubdivideIntoThirds
import at.orchaldir.gm.utils.math.halfSegment
import at.orchaldir.gm.utils.math.subdividePolygon
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.FULL_CIRCLE
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE

private val AT_TOP = -QUARTER_CIRCLE
private val SQUARE_ORIENTATION = QUARTER_CIRCLE / 2.0f
private val cutoffSubdivide = createSubdivideIntoThirds(1.0f / 5.0f)

// cross

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

// diamond

fun createDiamond(center: Point2d, radius: Distance) = createRegularPolygon(center, radius, 4)

fun createCutoffDiamond(center: Point2d, radius: Distance) =
    createCutoffRegularPolygon(center, radius, 4)

fun createRoundedDiamond(center: Point2d, radius: Distance) = createRoundedRegularPolygon(center, radius, 4)

// regular polygon

fun createRegularPolygon(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation = AT_TOP) =
    Polygon2d(createRegularPolygonPoints(center, radius, sides, firstCorner))

fun createCutoffRegularPolygon(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation = AT_TOP) =
    subdividePolygon(createRegularPolygon(center, radius, sides, firstCorner), 1, cutoffSubdivide)

fun createRoundedRegularPolygon(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation = AT_TOP) =
    Polygon2d(
        subdividePolygon(
            createRegularPolygonPoints(center, radius, sides, firstCorner),
            ::halfSegment,
        )
    )

fun createScallopedRegularPolygon(center: Point2d, radius: Distance, sides: Int, firstCorner: Orientation = AT_TOP) =
    Polygon2d(
        subdividePolygon(
            createRegularPolygonPoints(center, radius, sides, firstCorner),
            ::scallopSegment,
        )
    )

private fun scallopSegment(first: Point2d, second: Point2d, result: MutableList<Point2d>) {
    val half = (first + second) / 2.0f
    val diff = second - first
    val normal = diff.normal()

    result.add(half + normal / 5.0f)
    result.add(second)
}

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

// square

fun createSquare(center: Point2d, radius: Distance) =
    createRegularPolygon(center, radius, 4, SQUARE_ORIENTATION)

fun createCutoffSquare(center: Point2d, radius: Distance) =
    createCutoffRegularPolygon(center, radius, 4, SQUARE_ORIENTATION)

fun createRoundedSquare(center: Point2d, radius: Distance) =
    createRoundedRegularPolygon(center, radius, 4, SQUARE_ORIENTATION)

// teardrop

fun createTeardrop(center: Point2d, radius: Distance): Polygon2d {
    val aabb = AABB.fromRadii(center, radius / 2, radius)

    return Polygon2dBuilder()
        .addLeftPoint(aabb, CENTER, START, true)
        .addMirroredPoints(aabb, FULL, HALF)
        .addMirroredPoints(aabb, FULL, END)
        .reverse()
        .build()
}

fun createReverseTeardrop(center: Point2d, radius: Distance): Polygon2d {
    val aabb = AABB.fromRadii(center, radius / 2, radius)

    return Polygon2dBuilder()
        .addLeftPoint(aabb, CENTER, END, true)
        .addMirroredPoints(aabb, FULL, HALF)
        .addMirroredPoints(aabb, FULL, START)
        .build()
}

// triangle

fun createTriangle(center: Point2d, radius: Distance, firstCorner: Orientation = AT_TOP) =
    createRegularPolygon(center, radius, 3, firstCorner)

fun createCutoffTriangle(center: Point2d, radius: Distance) =
    createCutoffRegularPolygon(center, radius, 3)

fun createRoundedTriangle(center: Point2d, radius: Distance, firstCorner: Orientation = AT_TOP) =
    createRoundedRegularPolygon(center, radius, 3, firstCorner)
