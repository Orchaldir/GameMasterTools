package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

private val TWO = Factor(2.0f)

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(0.0f, 0.0f), size)
    constructor(x: Float, y: Float, width: Float, height: Float) : this(Point2d(x, y), Size2d(width, height))

    companion object {
        fun fromCenter(center: Point2d, size: Size2d) = AABB(
            center - size / TWO, size
        )

        fun fromCorners(start: Point2d, end: Point2d): AABB {
            val diff = end - start
            return AABB(
                start,
                Size2d(diff.x, diff.y)
            )
        }

        fun fromRadii(center: Point2d, radiusX: Distance, radiusY: Distance) =
            fromCenter(center, Size2d(radiusX * 2.0f, radiusY * 2.0f))
    }

    fun getCenter() = start + size / TWO

    fun getEnd() = start + size

    fun getInnerRadius() = Distance.fromMeters(minOf(size.width, size.height) / 2.0f)

    fun convertWidth(factor: Factor) = Distance.fromMeters(size.width * factor.value)

    fun convertHeight(factor: Factor) = Distance.fromMeters(size.height * factor.value)

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

    fun mirror(polygon: Polygon2d): Polygon2d {
        val mirrorX = start.x + size.width / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(2.0f * mirrorX - it.x, it.y) })
    }

    operator fun plus(offset: Point2d) = AABB(start + offset, size)

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
}

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getStartX(width: Factor) = Factor(0.5f - width.value / 2.0f)

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getEndX(width: Factor) = Factor(0.5f + width.value / 2.0f)
