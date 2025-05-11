package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.hypot

@Serializable
data class Point2d(val x: Distance = ZERO_DISTANCE, val y: Distance = ZERO_DISTANCE) {

    companion object {
        fun square(size: Float) = square(Distance.fromMeters(size))

        fun square(distance: Distance) = Point2d(distance, distance)

        fun fromMeters(x: Long, y: Long) = Point2d(
            Distance.fromMeters(x),
            Distance.fromMeters(y),
        )

        fun fromMeters(x: Float, y: Float) = Point2d(
            Distance.fromMeters(x),
            Distance.fromMeters(y),
        )

        fun xAxis(distance: Distance) = Point2d(x = distance)
        fun yAxis(distance: Distance) = Point2d(y = distance)
    }

    fun addWidth(distance: Distance) = Point2d(x + distance, y)
    fun addHeight(distance: Distance) = Point2d(x, y + distance)

    fun minusWidth(distance: Distance) = Point2d(x - distance, y)
    fun minusHeight(distance: Distance) = Point2d(x, y - distance)

    fun createPolar(distance: Distance, orientation: Orientation) = Point2d(
        x + distance * orientation.cos(),
        y + distance * orientation.sin(),
    )

    fun length() = Distance.fromMeters(hypot(x.toMeters(), y.toMeters()))
    fun calculateDistance(other: Point2d) = minus(other).length()

    fun normal() = Point2d(-y, x)

    fun normalize(): Point2d {
        val length = length()

        if (length.value() > 0) {
            return this / length.toMeters()
        }

        return square(0.0f)
    }

    fun calculateOrientation() = Orientation.fromRadians(
        atan2(
            y.toMeters().toDouble(),
            x.toMeters().toDouble(),
        ).toFloat()
    )
    fun calculateOrientation(other: Point2d) = (other - this).calculateOrientation()

    operator fun plus(distance: Distance) = Point2d(x + distance, y + distance)
    operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
    operator fun plus(size: Size2d) = Point2d(x + size.width, y + size.height)

    operator fun minus(distance: Distance) = Point2d(x - distance, y - distance)
    operator fun minus(other: Point2d) = Point2d(x - other.x, y - other.y)
    operator fun minus(size: Size2d) = Point2d(x - size.width, y - size.height)

    operator fun times(factor: Int) = Point2d(x * factor, y * factor)
    operator fun times(factor: Float) = Point2d(x * factor, y * factor)
    operator fun times(distance: Distance) = times(distance.toMeters())
    operator fun div(factor: Float) = Point2d(x / factor, y / factor)
}
