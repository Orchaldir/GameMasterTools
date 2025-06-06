package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Orientation
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

    return PathBuilder()
        .moveTo(start.x, start.y)
        .ellipticalArc(end.x, end.y, radius, radius)
        .close()
        .build()
}

fun convertHollowRectangleToPath(
    center: Point2d,
    width: Distance,
    height: Distance,
    thickness: Distance,
): String {
    val halfWidth = width / 2.0f
    val halfHeight = height / 2.0f
    val halfInnerWidth = halfWidth - thickness
    val halfInnerHeight = halfHeight - thickness

    return PathBuilder()
        .moveTo(center.x - halfWidth, center.y - halfHeight)
        .lineTo(center.x - halfWidth, center.y + halfHeight)
        .lineTo(center.x + halfWidth, center.y + halfHeight)
        .lineTo(center.x + halfWidth, center.y - halfHeight)
        .close()
        .moveTo(center.x - halfInnerWidth, center.y - halfInnerHeight)
        .lineTo(center.x + halfInnerWidth, center.y - halfInnerHeight)
        .lineTo(center.x + halfInnerWidth, center.y + halfInnerHeight)
        .lineTo(center.x - halfInnerWidth, center.y + halfInnerHeight)
        .close()
        .build()
}

fun convertLineToPath(line: List<Point2d>) = convertCornersToPath(line)
    .build()

fun convertPointedOvalToPath(center: Point2d, radiusX: Distance, radiusY: Distance): String {
    val metersX = radiusX.toMeters()
    val metersY = radiusY.toMeters()
    val radius = fromMeters(metersX.pow(2.0f) + metersY.pow(2.0f)) / (2.0f * min(metersX, metersY))
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

    return PathBuilder()
        .moveTo(center.x, center.y - outerRadius)
        .ellipticalArc(center.x, center.y + outerRadius, outerRadius, outerRadius, largeArcFlag = true)
        .ellipticalArc(center.x, center.y - outerRadius, outerRadius, outerRadius, largeArcFlag = true)
        .close()
        .moveTo(center.x, center.y - innerRadius)
        .ellipticalArc(
            center.x,
            center.y + innerRadius,
            innerRadius,
            innerRadius,
            largeArcFlag = true,
            sweepFlag = true
        )
        .ellipticalArc(
            center.x,
            center.y - innerRadius,
            innerRadius,
            innerRadius,
            largeArcFlag = true,
            sweepFlag = true
        )
        .close()
        .build()
}

fun convertRoundedPolygonToPath(polygon: Polygon2d) =
    convertRoundedPolygonToPath(polygon.corners, PathBuilder())

fun convertRoundedPolygonToPath(corners: List<Point2d>, builder: PathBuilder): String {
    var previous = corners[0]
    var isStart = true
    var isSharp = false
    var firstMiddle: Point2d? = null

    for (i in 0..<corners.size) {
        val index = (i + 1) % corners.size
        val corner = corners[index]

        if (previous.calculateDistance(corner).isZero()) {
            isSharp = true

            if (!isStart) {
                builder.lineTo(previous)
            }

            continue
        }

        if (isStart) {
            isStart = false
            val middle = (previous + corner) / 2.0f

            if (isSharp) {
                isSharp = false
                builder.moveTo(previous)
                builder.lineTo(middle)
            } else {
                firstMiddle = middle
                builder.moveTo(middle)
            }
        } else if (isSharp) {
            isSharp = false
            val middle = (previous + corner) / 2.0f
            builder.lineTo(middle)
        } else {
            val middle = (previous + corner) / 2.0f
            builder.curveTo(previous, middle)
        }

        previous = corner
    }

    if (firstMiddle != null) {
        builder.curveTo(previous, firstMiddle)
    } else {
        builder.close()
    }

    return builder.build()
}

fun convertPolygonToPath(polygon: Polygon2d) = convertCornersToPath(polygon.corners)
    .close()
    .build()

fun convertPolygonWithHoleToPath(polygon: Polygon2d, hole: Polygon2d): String {
    val builder = PathBuilder()

    convertCornersToPath(polygon.corners, builder)

    builder.close()

    convertCornersToPath(hole.corners.reversed(), builder)

    return builder.close().build()
}

fun convertRoundedPolygonWithHoleToPath(polygon: Polygon2d, hole: Polygon2d): String {
    val builder = PathBuilder()

    convertRoundedPolygonToPath(polygon.corners, builder)

    builder.close()

    convertCornersToPath(hole.corners.reversed(), builder)

    return builder.close().build()
}

fun convertPolygonWithRoundedHoleToPath(polygon: Polygon2d, hole: Polygon2d): String {
    val builder = PathBuilder()

    convertCornersToPath(polygon.corners, builder)

    builder.close()

    convertRoundedPolygonToPath(hole.corners.reversed(), builder)

    return builder.close().build()
}

fun convertRoundedPolygonWithRoundedHoleToPath(polygon: Polygon2d, hole: Polygon2d): String {
    val builder = PathBuilder()

    convertRoundedPolygonToPath(polygon.corners, builder)

    builder.close()

    convertRoundedPolygonToPath(hole.corners.reversed(), builder)

    return builder.close().build()
}

fun convertRoundedPolygonWithRoundedHolesToPath(polygon: Polygon2d, holes: List<Polygon2d>): String {
    val builder = PathBuilder()

    convertRoundedPolygonToPath(polygon.corners, builder)

    builder.close()

    holes.forEach { hole ->
        convertRoundedPolygonToPath(hole.corners.reversed(), builder)

        builder.close()
    }

    return builder.build()
}

private fun convertCornersToPath(corners: List<Point2d>) = convertCornersToPath(corners, PathBuilder())

private fun convertCornersToPath(corners: List<Point2d>, builder: PathBuilder): PathBuilder {
    builder.moveTo(corners[0])

    corners.stream()
        .skip(1)
        .forEach { builder.lineTo(it) }

    return builder
}

