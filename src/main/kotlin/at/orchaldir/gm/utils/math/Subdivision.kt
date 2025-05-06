package at.orchaldir.gm.utils.math

// line

fun subdivideLine(
    line: Line2d,
    iterations: Int,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit = subdivideIntoThirds,
) = Line2d(subdivideLine(line.points, iterations, updateSegment))

fun subdivideLine(
    points: List<Point2d>,
    iterations: Int,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit = subdivideIntoThirds,
): List<Point2d> {
    var result = points

    repeat(iterations) {
        result = subdivideLine(result, updateSegment)
    }

    return result
}

fun subdivideLine(
    points: List<Point2d>,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit,
): List<Point2d> {
    if (points.size < 3) {
        return points
    }

    val iter = points.iterator()
    var first = iter.next()
    val result = mutableListOf(first)

    while (iter.hasNext()) {
        val second = iter.next()

        updateSegment(first, second, result)

        first = second
    }

    result.add(first)

    return result
}

// polygon

fun subdividePolygon(
    polygon: Polygon2d,
    iterations: Int,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit = subdivideIntoThirds,
) = Polygon2d(subdividePolygon(polygon.corners, iterations, updateSegment))

fun subdividePolygon(
    points: List<Point2d>,
    iterations: Int,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit = subdivideIntoThirds,
): List<Point2d> {
    var result = points

    repeat(iterations) {
        result = subdividePolygon(result, updateSegment)
    }

    return result
}

fun subdividePolygon(
    points: List<Point2d>,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit,
): List<Point2d> {
    if (points.size < 3) {
        return points
    }

    val iter = points.iterator()
    var first = iter.next()
    val result = mutableListOf<Point2d>()

    while (iter.hasNext()) {
        val second = iter.next()

        updateSegment(first, second, result)

        first = second
    }

    val second = points.first()

    updateSegment(first, second, result)

    return result
}

// segments

val subdivideIntoThirds = createSubdivideIntoThirds(1.0f / 3.0f)

fun createSubdivideIntoThirds(factor: Float): (Point2d, Point2d, MutableList<Point2d>) -> Unit =
    { first, second, result ->
        val diff = second - first
        val new0 = first + diff * factor
        val new1 = first + diff * (1.0f - factor)

    result.add(new0)
    result.add(new1)
}

fun halfSegment(first: Point2d, second: Point2d, result: MutableList<Point2d>) {
    result.add((first + second) / 2.0f)
    result.add(second)
}