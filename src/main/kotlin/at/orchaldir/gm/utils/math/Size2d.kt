package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.checkDistance
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class Size2d(val width: Distance, val height: Distance) {

    init {
        require(width.value() > 0) { "Width muster be greater 0!" }
        require(height.value() > 0) { "Height muster be greater 0!" }
    }

    companion object {
        fun fromMeters(width: Float, height: Float) =
            Size2d(Distance.fromMeters(width), Distance.fromMeters(height))

        fun fromMillimeters(width: Long, height: Long) = Size2d(
            Distance.fromMillimeters(width),
            Distance.fromMillimeters(height),
        )

        fun square(distance: Distance) = Size2d(distance, distance)

        fun fromDiagonalRadius(radius: Distance) = square(radius * sqrt(2.0f))
    }

    fun scale(horizontal: Factor, vertical: Factor) =
        Size2d(width * horizontal, height * vertical)

    fun addWidth(distance: Distance) = Size2d(width + distance, height)
    fun addHeight(distance: Distance) = Size2d(width, height + distance)

    operator fun plus(distance: Distance) = Size2d(width + distance, height + distance)
    operator fun plus(size: Size2d) = Size2d(width + size.width, height + size.height)
    operator fun minus(distance: Distance) = Size2d(width - distance, height - distance)
    operator fun times(factor: Factor) = Size2d(width * factor, height * factor)
    operator fun div(factor: Factor) = Size2d(width / factor, height / factor)

    fun replaceWidth(width: Distance) = copy(width = width)
    fun replaceHeight(factor: Factor) = copy(height = height * factor)

    fun max(other: Size2d) = Size2d(width.max(other.width), height.max(other.height))

    fun minSize() = width.min(height)
    fun maxSize() = width.max(height)

    fun innerRadius() = minSize() / 2.0f
}

fun checkSize(size: Size2d, label: String, min: Distance, max: Distance) {
    checkDistance(size.width, "$label's width", min, max)
    checkDistance(size.height, "$label's height", min, max)
}
