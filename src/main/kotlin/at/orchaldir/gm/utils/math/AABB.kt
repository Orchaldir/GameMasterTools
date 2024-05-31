package at.orchaldir.gm.utils.math

/**
 * An axis aligned bounding box.
 */
class AABB(val start: Point2d, val size: Size2d) {

    fun getCenter() = start + size / 2.0f

    fun getInnerRadius() = Distance(minOf(size.width, size.height) / 2.0f)
}
