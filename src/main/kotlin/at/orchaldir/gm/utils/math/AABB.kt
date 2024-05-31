package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(0.0f, 0.0f), size)

    fun getCenter() = start + size / 2.0f

    fun getInnerRadius() = Distance(minOf(size.width, size.height) / 2.0f)
}
