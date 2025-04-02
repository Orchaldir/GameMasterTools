package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.hypot

@Serializable
data class Point2d(val x: Float = 0.0f, val y: Float = 0.0f) {

    constructor(x: Distance, y: Distance) : this(x.toMeters(), y.toMeters())

    fun addWidth(distance: Distance) = Point2d(x + distance.toMeters(), y)
    fun addHeight(distance: Distance) = Point2d(x, y + distance.toMeters())

    fun minusWidth(distance: Distance) = Point2d(x - distance.toMeters(), y)
    fun minusHeight(distance: Distance) = Point2d(x, y - distance.toMeters())

    fun createPolar(distance: Distance, orientation: Orientation) = Point2d(
        x + distance.toMeters() * orientation.cos(),
        y + distance.toMeters() * orientation.sin(),
    )

    fun length() = hypot(x, y)
    fun calculateDistance(other: Point2d) = minus(other).length()

    fun calculateOrientation() = Orientation.fromRadians(atan2(y.toDouble(), x.toDouble()).toFloat())
    fun calculateOrientation(other: Point2d) = (other - this).calculateOrientation()

    fun calculateSlope(other: Point2d) = (other.y - y) / (other.x - x)

    operator fun plus(distance: Distance) = Point2d(x + distance.toMeters(), y + distance.toMeters())
    operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
    operator fun plus(size: Size2d) = Point2d(x + size.width, y + size.height)

    operator fun minus(distance: Distance) = Point2d(x - distance.toMeters(), y - distance.toMeters())
    operator fun minus(other: Point2d) = Point2d(x - other.x, y - other.y)
    operator fun minus(size: Size2d) = Point2d(x - size.width, y - size.height)

    operator fun times(factor: Int) = Point2d(x * factor, y * factor)
    operator fun times(factor: Float) = Point2d(x * factor, y * factor)
    operator fun div(factor: Float) = Point2d(x / factor, y / factor)
}
