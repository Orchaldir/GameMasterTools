package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable
import kotlin.math.hypot

@Serializable
data class Point2d(val x: Float = 0.0f, val y: Float = 0.0f) {

    constructor(x: Distance, y: Distance) : this(x.toMeters(), y.toMeters())

    fun createPolar(distance: Distance, orientation: Orientation) = Point2d(
        x + distance.toMeters() * orientation.cos(),
        y + distance.toMeters() * orientation.sin(),
    )

    fun length() = hypot(x, y)
    fun calculateDistance(other: Point2d) = minus(other).length()

    operator fun plus(distance: Distance) = Point2d(x + distance.toMeters(), y + distance.toMeters())
    operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
    operator fun plus(size: Size2d) = Point2d(x + size.width, y + size.height)

    operator fun minus(other: Point2d) = Point2d(x - other.x, y - other.y)
    operator fun minus(size: Size2d) = Point2d(x - size.width, y - size.height)

    operator fun times(factor: Int) = Point2d(x * factor, y * factor)
    operator fun div(factor: Float) = Point2d(x / factor, y / factor)

}
