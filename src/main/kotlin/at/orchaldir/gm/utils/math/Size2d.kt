package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

@Serializable
data class Size2d(val width: Distance, val height: Distance) {

    init {
        require(width.value() > 0) { "Width muster be greater 0!" }
        require(height.value() > 0) { "Height muster be greater 0!" }
    }

    companion object {
        fun fromMeters(width: Float, height: Float) =
            Size2d(Distance.fromMeters(width), Distance.fromMeters(height))

        fun square(distance: Distance) = Size2d(distance, distance)
    }

    fun scale(horizontal: Factor, vertical: Factor) =
        Size2d(width * horizontal.toNumber(), height * vertical.toNumber())

    fun addWidth(distance: Distance) = Size2d(width + distance, height)
    fun addHeight(distance: Distance) = Size2d(width, height + distance)

    operator fun plus(distance: Distance) = Size2d(width + distance, height + distance)
    operator fun plus(size: Size2d) = Size2d(width + size.width, height + size.height)
    operator fun minus(distance: Distance) = Size2d(width - distance, height - distance)
    operator fun times(factor: Factor) = Size2d(width * factor.toNumber(), height * factor.toNumber())
    operator fun div(factor: Factor) = Size2d(width / factor.toNumber(), height / factor.toNumber())

    fun replaceWidth(width: Distance) = copy(width = width)

    fun max(other: Size2d) = Size2d(width.max(other.width), height.max(other.height))

    fun minSize() = width.min(height)
    fun maxSize() = width.max(height)
}
