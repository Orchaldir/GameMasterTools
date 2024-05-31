package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(0.0f, 0.0f), size)

    companion object {
        fun fromCenter(center: Point2d, size: Size2d) = AABB(
            center - size / 2.0f, size
        )
    }

    fun getCenter() = start + size / 2.0f

    fun getInnerRadius() = Distance(minOf(size.width, size.height) / 2.0f)

    fun getPoint(horizontal: Float, vertical: Float) = Point2d(
        start.x + size.width * horizontal,
        start.y + size.height * vertical,
    )

    fun shrink(border: Distance) = AABB(start + border, size - border * 2.0f)
}
