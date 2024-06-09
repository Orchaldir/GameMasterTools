package at.orchaldir.gm.utils.math

fun splitLine(start: Point2d, end: Point2d, parts: Int): List<Point2d> {
    require(parts > 0) { "Parts needs to be greater than 0!" }
    val diff = end - start
    val step = diff / parts.toFloat()
    val points = mutableListOf(start)
    var point = start

    for (i in 0..<parts) {
        point += step
        points.add(point)
    }

    points.add(end)

    return points
}