package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import java.lang.Float.min
import kotlin.math.pow

fun convertCircleArcToPath(
    center: Point2d,
    radius: Distance,
    offset: Orientation,
    angle: Orientation,
): String {
    val start = center.createPolar(radius, offset)
    val end = center.createPolar(radius, offset + angle)
    val radiusMeter = radius.toMeters()

    return PathBuilder()
        .moveTo(start.x, start.y)
        .ellipticalArc(end.x, end.y, radiusMeter, radiusMeter)
        .close()
        .build()
}

fun convertLineToPath(line: List<Point2d>): String {
    val path = convertCornersToPath(line)

    return path.toString()
}

fun convertPointedOvalToPath(center: Point2d, radiusX: Distance, radiusY: Distance): String {
    val metersX = radiusX.toMeters()
    val metersY = radiusY.toMeters()
    val radius = (metersX.pow(2.0f) + metersY.pow(2.0f)) / (2.0f * min(metersX, metersY))
    val aabb = AABB.fromRadii(center, radiusX, radiusY)
    val left = if (metersX > metersY) {
        aabb.getPoint(START, CENTER)
    } else {
        aabb.getPoint(CENTER, START)
    }
    val right = if (metersX > metersY) {
        aabb.getPoint(END, CENTER)
    } else {
        aabb.getPoint(CENTER, END)
    }

    return PathBuilder()
        .moveTo(left.x, left.y)
        .ellipticalArc(right.x, right.y, radius, radius)
        .ellipticalArc(left.x, left.y, radius, radius)
        .close()
        .build()
}

fun convertRingToPath(
    center: Point2d,
    outerRadius: Distance,
    innerRadius: Distance,
): String {
    val outer = outerRadius.toMeters()
    val inner = innerRadius.toMeters()

    return PathBuilder()
        .moveTo(center.x, center.y - outer)
        .ellipticalArc(center.x, center.y + outer, outer, outer, largeArcFlag = true)
        .ellipticalArc(center.x, center.y - outer, outer, outer, largeArcFlag = true)
        .close()
        .moveTo(center.x, center.y - inner)
        .ellipticalArc(center.x, center.y + inner, inner, inner, largeArcFlag = true, sweepFlag = true)
        .ellipticalArc(center.x, center.y - inner, inner, inner, largeArcFlag = true, sweepFlag = true)
        .close()
        .build()
}

fun convertRoundedPolygonToPath(polygon: Polygon2d): String {
    val path = StringBuilder()
    var previous = polygon.corners[0]
    var isStart = true
    var isSharp = false
    var firstMiddle: Point2d? = null

    for (i in 0..polygon.corners.size) {
        val index = (i + 1) % polygon.corners.size
        val corner = polygon.corners[index]

        if (previous.calculateDistance(corner) == 0.0f) {
            isSharp = true

            if (!isStart) {
                lineTo(path, previous)
            }

            continue
        }

        if (isStart) {
            isStart = false
            val middle = (previous + corner) / 2.0f

            if (isSharp) {
                isSharp = false
                moveTo(path, previous)
                lineTo(path, middle)
            } else {
                firstMiddle = middle
                moveTo(path, middle)
            }
        } else if (isSharp) {
            isSharp = false
            val middle = (previous + corner) / 2.0f
            lineTo(path, middle)
        } else {
            val middle = (previous + corner) / 2.0f
            curveTo(path, previous, middle)
        }

        previous = corner
    }

    if (firstMiddle != null) {
        curveTo(path, previous, firstMiddle)
    } else {
        close(path)
    }

    return path.toString()
}

fun convertPolygonToPath(polygon: Polygon2d): String {
    val path = convertCornersToPath(polygon.corners)

    close(path)

    return path.toString()
}

private fun convertCornersToPath(corners: List<Point2d>): StringBuilder {
    val path = StringBuilder()

    moveTo(path, corners[0])

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

private fun curveTo(path: StringBuilder, control: Point2d, end: Point2d) {
    path.append(" Q ")
        .append(control.x)
        .append(" ")
        .append(control.y)
        .append(" ")
        .append(end.x)
        .append(" ")
        .append(end.y)
}

fun close(path: StringBuilder) {
    path.append(" Z")
}