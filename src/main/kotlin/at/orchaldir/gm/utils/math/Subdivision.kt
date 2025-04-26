package at.orchaldir.gm.utils.math


fun subdivideLine(
    line: Line2d,
    iterations: Int,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit = ::subdivideIntoThirds,
) = Line2d(subdividePoints(line.points, iterations, updateSegment))

fun subdividePoints(
    points: List<Point2d>,
    iterations: Int,
    updateSegment: (Point2d, Point2d, MutableList<Point2d>) -> Unit = ::subdivideIntoThirds,
): List<Point2d> {
    var result = points

    repeat(iterations) {
        result = subdividePoints(result, updateSegment)
    }

    return result
}

fun subdividePoints(
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

fun subdivideIntoThirds(first: Point2d, second: Point2d, result: MutableList<Point2d>) {
    val diff = (second - first) / 3.0f
    val new0 = first + diff
    val new1 = new0 + diff

    result.add(new0)
    result.add(new1)
}

fun halfSegment(first: Point2d, second: Point2d, result: MutableList<Point2d>) {
    result.add((first + second) / 2.0f)
}