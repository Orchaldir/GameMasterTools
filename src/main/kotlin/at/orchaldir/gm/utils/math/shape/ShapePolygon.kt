package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.FULL_CIRCLE
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE

val CROSS_Y = fromPercentage(33)
private val AT_TOP = -QUARTER_CIRCLE
private val SQUARE_ORIENTATION = QUARTER_CIRCLE / 2.0f
private val cutoffSubdivide = createSubdivideIntoThirds(1.0f / 5.0f)

// cross

fun createCross(center: Point2d, height: Distance): Polygon2d {
    val aabb = AABB.fromRadii(center, height / 4.0f, height / 2.0f)
    return createCross(aabb)
}

fun createCross(aabb: AABB): Polygon2d {
    val size = fromPercentage(15)
    val halfSize = size / 2.0f
    val half = aabb.convertHeight(halfSize)
    val top = aabb.getPoint(HALF, START)
    val left = aabb.getPoint(START, CROSS_Y)
    val center = aabb.getPoint(HALF, CROSS_Y)
    val bottom = aabb.getPoint(HALF, END)

    return Polygon2dBuilder()
        .addVerticallyMirroredPoint(aabb, top.minusWidth(half))
        .addVerticallyMirroredPoint(aabb, center.minus(half))
        .addVerticallyMirroredPoint(aabb, left.minusHeight(half))
        .addVerticallyMirroredPoint(aabb, left.addHeight(half))
        .addVerticallyMirroredPoint(aabb, center.addHeight(half).minusWidth(half))
        .addVerticallyMirroredPoint(aabb, bottom.minusWidth(half))
        .build()
}

// diamond

fun createDiamond(center: Point2d, radius: Distance) = createRegularPolygon(center, radius, 4)

fun createCutoffDiamond(center: Point2d, radius: Distance) =
    createCutoffRegularPolygon(center, radius, 4)

fun createRoundedDiamond(center: Point2d, radius: Distance) = createRoundedRegularPolygon(center, radius, 4)

// heater (shield)

fun createHeater(center: Point2d, radius: Distance): Polygon2d {
    val aabb = AABB.fromRadii(center, radius, radius)

    return createHeater(aabb)
}

fun createHeater(aabb: AABB) = Polygon2dBuilder()
    .addMirroredPoints(aabb, FULL, START, true)
    .addMirroredPoints(aabb, FULL, END)
    .addLeftPoint(aabb, CENTER, END, true)
    .reverse()
    .build()

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

    return createTeardrop(aabb)
}

fun createTeardrop(aabb: AABB) = Polygon2dBuilder()
    .addLeftPoint(aabb, CENTER, START, true)
    .addMirroredPoints(aabb, FULL, HALF)
    .addMirroredPoints(aabb, FULL, END)
    .reverse()
    .build()

fun createReverseTeardrop(center: Point2d, radius: Distance): Polygon2d {
    val aabb = AABB.fromRadii(center, radius / 2, radius)

    return createReverseTeardrop(aabb)
}

fun createReverseTeardrop(aabb: AABB): Polygon2d = Polygon2dBuilder()
    .addLeftPoint(aabb, CENTER, END, true)
    .addMirroredPoints(aabb, FULL, HALF)
    .addMirroredPoints(aabb, FULL, START)
    .build()

// triangle

fun createTriangle(center: Point2d, radius: Distance, firstCorner: Orientation = AT_TOP) =
    createRegularPolygon(center, radius, 3, firstCorner)

fun createCutoffTriangle(center: Point2d, radius: Distance) =
    createCutoffRegularPolygon(center, radius, 3)

fun createRoundedTriangle(center: Point2d, radius: Distance, firstCorner: Orientation = AT_TOP) =
    createRoundedRegularPolygon(center, radius, 3, firstCorner)
