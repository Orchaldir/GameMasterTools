package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

private val TWO = Factor(2.0f)

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(0.0f, 0.0f), size)

    companion object {
        fun fromCenter(center: Point2d, size: Size2d) = AABB(
            center - size / TWO, size
        )

        fun fromRadii(center: Point2d, radiusX: Distance, radiusY: Distance) =
            fromCenter(center, Size2d(radiusX.value * 2.0f, radiusY.value * 2.0f))
    }

    fun getCenter() = start + size / TWO

    fun getInnerRadius() = Distance(minOf(size.width, size.height) / 2.0f)

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

    fun shrink(border: Distance) = AABB(start + border, size - border * 2.0f)

    fun shrink(factor: Factor): AABB {
        val border = size * (factor * 0.5f)
        return AABB(start + border, size * factor)
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
