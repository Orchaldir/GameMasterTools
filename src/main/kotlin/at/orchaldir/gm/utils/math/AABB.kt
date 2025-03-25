package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable
import kotlin.math.min

private val TWO = Factor(2.0f)

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(0.0f, 0.0f), size)
    constructor(x: Float, y: Float, width: Float, height: Float) : this(Point2d(x, y), Size2d(width, height))
    constructor(x: Float, y: Float, size: Size2d) : this(Point2d(x, y), size)

    companion object {
        fun fromCenter(center: Point2d, size: Size2d) = AABB(
            center - size / TWO, size
        )

        fun fromCenter(center: Point2d, size: Distance) = fromWidthAndHeight(center, size, size)

        fun fromCorners(start: Point2d, end: Point2d): AABB {
            val diff = end - start
            return AABB(
                start,
                Size2d(diff.x, diff.y)
            )
        }

        fun fromRadii(center: Point2d, radiusX: Distance, radiusY: Distance) =
            fromWidthAndHeight(center, radiusX * 2.0f, radiusY * 2.0f)

        fun fromWidthAndHeight(center: Point2d, width: Distance, height: Distance) =
            fromCenter(center, Size2d(width, height))
    }

    fun getCenter() = start + size / TWO

    fun getEnd() = start + size

    fun getInnerRadius() = Distance.fromMeters(minOf(size.width, size.height) / 2.0f)

    fun convertWidth(factor: Factor) = convertSide(size.width, factor)

    fun convertHeight(factor: Factor) = convertSide(size.height, factor)

    fun convertMinSide(factor: Factor) = convertSide(min(size.width, size.height), factor)

    private fun convertSide(side: Float, factor: Factor) = Distance.fromMeters(side * factor.value)

    fun getCorners(): List<Point2d> {
        return listOf(
            getPoint(START, START),
            getPoint(END, START),
            getPoint(END, END),
            getPoint(START, END),
        )
    }

    fun getPoint(horizontal: Factor, vertical: Factor) = Point2d(
        start.x + size.width * horizontal.value,
        start.y + size.height * vertical.value,
    )

    fun getMirroredPoints(width: Factor, vertical: Factor): Pair<Point2d, Point2d> {
        return Pair(
            getPoint(getStartX(width), vertical),
            getPoint(getEndX(width), vertical)
        )
    }

    fun mirrorHorizontally(polygon: Polygon2d): Polygon2d {
        val mirrorY = start.y + size.height / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(it.x, 2.0f * mirrorY - it.y) })
    }

    fun mirrorVertically(polygon: Polygon2d): Polygon2d {
        val mirrorX = start.x + size.width / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(2.0f * mirrorX - it.x, it.y) })
    }

    operator fun plus(offset: Point2d) = AABB(start + offset, size)

    fun replaceWidth(width: Distance) = copy(size = size.replaceWidth(width))

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
fun getStartX(width: Factor) = Factor(0.5f - width.value / 2.0f)

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getEndX(width: Factor) = Factor(0.5f + width.value / 2.0f)
