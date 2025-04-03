package at.orchaldir.gm.utils.math

fun subdivideLine(line: Line2d, iterations: Int) = Line2d(subdivideLine(line.points, iterations))

fun subdivideLine(points: List<Point2d>, iterations: Int): List<Point2d> {
    var result = points

    repeat(iterations) {
        result = subdivideLine(result)
    }

    return result
}

fun subdivideLine(points: List<Point2d>): List<Point2d> {
    if (points.size < 3) {
        return points
    }

    val iter = points.iterator()
    var first = iter.next()
    val result = mutableListOf(first)

    while (iter.hasNext()) {
        val second = iter.next()
        val diff = (second - first) / 3.0f
        val new0 = first + diff
        val new1 = new0 + diff

        result.add(new0)
        result.add(new1)

        first = second
    }

    result.add(first)

    return result
}