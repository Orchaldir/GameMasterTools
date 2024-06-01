package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d

fun convertLineToPath(line: List<Point2d>): String {
    val path = convertCornersToPath(line);

    return path.toString()
}

fun convertPolygonToPath(polygon: Polygon2d): String {
    val path = convertCornersToPath(polygon.corners);

    close(path)

    return path.toString()
}

fun convertCornersToPath(corners: List<Point2d>): StringBuilder {
    val path = StringBuilder()

    moveTo(path, corners[0]);

    corners.stream()
        .skip(1)
        .forEach { lineTo(path, it) }

    return path
}

private fun moveTo(path: StringBuilder, point: Point2d) {
    path.append("M ")
        .append(point.x)
        .append(" ")
        .append(point.y)
}

private fun lineTo(path: StringBuilder, point: Point2d) {
    path.append(" L ")
        .append(point.x)
        .append(" ")
        .append(point.y)
}

fun close(path: StringBuilder) {
    path.append(" Z")
}