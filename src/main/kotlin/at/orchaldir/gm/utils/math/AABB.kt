package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

private val TWO = fromPercentage(200)

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(), size)
    constructor(x: Float, y: Float, width: Distance, height: Distance) : this(
        Point2d.fromMeters(x, y),
        Size2d(width, height)
    )

    constructor(x: Float, y: Float, size: Size2d) : this(Point2d.fromMeters(x, y), size)

    companion object {
        fun fromBottom(bottom: Point2d, size: Size2d) = AABB(
            bottom.minusWidth(size.height / 2), size
        )

        fun fromCenter(center: Point2d, size: Size2d) = AABB(
            center - size / TWO, size
        )

        fun fromCenter(center: Point2d, size: Distance) = fromWidthAndHeight(center, size, size)

        fun fromCorners(start: Point2d, end: Point2d): AABB {
            val diff = end - start
            return AABB(
                start,
                Size2d(diff.x, diff.y),
            )
        }

        fun fromMeters(x: Float, y: Float, width: Float, height: Float) =
            AABB(Point2d.fromMeters(x, y), Size2d.fromMeters(width, height))

        fun fromRadius(center: Point2d, radius: Distance) =
            fromRadii(center, radius, radius)

        fun fromRadii(center: Point2d, radiusX: Distance, radiusY: Distance) =
            fromWidthAndHeight(center, radiusX * 2.0f, radiusY * 2.0f)

        fun fromWidthAndHeight(center: Point2d, width: Distance, height: Distance) =
            fromCenter(center, Size2d(width, height))
    }

    fun getCenter() = start + size / TWO

    fun getEnd() = start + size

    fun getInnerRadius() = size.innerRadius()

    fun convertWidth(factor: Factor) = size.width * factor

    fun convertHeight(factor: Factor) = size.height * factor

    fun convertMinSide(factor: Factor) = size.minSize() * factor

    fun getCorners(): List<Point2d> {
        return listOf(
            getPoint(START, START),
            getPoint(END, START),
            getPoint(END, END),
            getPoint(START, END),
        )
    }

    fun getPolygon() = Polygon2d(getCorners())

    fun getPoint(horizontal: Factor, vertical: Factor) = Point2d(
        start.x + size.width * horizontal,
        start.y + size.height * vertical,
    )

    fun getMirroredPoints(width: Factor, vertical: Factor): Pair<Point2d, Point2d> {
        return Pair(
            getPoint(getStartX(width), vertical),
            getPoint(getEndX(width), vertical)
        )
    }

    fun mirrorHorizontally(polygon: Polygon2d): Polygon2d {
        val mirrorY = start.y + size.height / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(it.x, mirrorY * 2.0f - it.y) })
    }

    fun mirrorVertically(polygon: Polygon2d): Polygon2d {
        val mirrorX = start.x + size.width / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(mirrorX * 2.0f - it.x, it.y) })
    }

    fun mirrorVertically(point: Point2d): Point2d {
        val mirrorX = start.x + size.width / 2.0f

        return Point2d(mirrorX * 2.0f - point.x, point.y)
    }

    operator fun plus(offset: Point2d) = AABB(start + offset, size)

    fun replaceWidth(width: Distance) = copy(size = size.replaceWidth(width))

    /**
     * Move the border outward by a certain distance.
     */
    fun grow(border: Distance) = AABB(start - border, size + border * 2.0f)

    /**
     * Move the border inward by a certain distance.
     */
    fun shrink(border: Distance) = AABB(start + border, size - border * 2.0f)

    /**
     * Shrink the area around the center by a certain percentage.
     */
    fun shrink(factor: Factor): AABB {
        val border = size * (factor * 0.5f)
        return AABB(start + border, size * (FULL - factor))
    }

    fun splitHorizontal(start: Factor, end: Factor): AABB {
        val startPoint = getPoint(start, START)
        val endPoint = getPoint(end, END)

        return fromCorners(startPoint, endPoint)
    }

    fun createSubAabb(horizontal: Factor, vertical: Factor, width: Factor, height: Factor) =
        fromWidthAndHeight(getPoint(horizontal, vertical), convertWidth(width), convertHeight(height))
}

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getStartX(width: Factor) = fromNumber(0.5f - width.toNumber() / 2.0f)

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getEndX(width: Factor) = fromNumber(0.5f + width.toNumber() / 2.0f)
