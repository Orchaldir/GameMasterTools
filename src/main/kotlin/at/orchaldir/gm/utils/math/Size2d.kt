package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class Size2d(val width: Float, val height: Float) {

    constructor(width: Distance, height: Distance) : this(width.toMeters(), height.toMeters())

    init {
        require(width > 0.0) { "Width muster be greater 0!" }
        require(height > 0.0) { "Height muster be greater 0!" }
    }

    companion object {
        fun square(size: Float) = Size2d(size, size)

        fun square(distance: Distance) = square(distance.toMeters())
    }

    fun scale(horizontal: Factor, vertical: Factor) =
        Size2d(width * horizontal.toNumber(), height * vertical.toNumber())

    fun addWidth(distance: Distance) = Size2d(width + distance.toMeters(), height)
    fun addWidth(distance: Float) = Size2d(width + distance, height)
    fun addHeight(distance: Distance) = Size2d(width, height + distance.toMeters())
    fun addHeight(distance: Float) = Size2d(width, height + distance)

    operator fun plus(distance: Distance) = Size2d(width + distance.toMeters(), height + distance.toMeters())
    operator fun plus(size: Size2d) = Size2d(width + size.width, height + size.height)
    operator fun minus(distance: Distance) = Size2d(width - distance.toMeters(), height - distance.toMeters())
    operator fun times(factor: Factor) = Size2d(width * factor.toNumber(), height * factor.toNumber())
    operator fun div(factor: Factor) = Size2d(width / factor.toNumber(), height / factor.toNumber())

    fun replaceWidth(width: Distance) = copy(width = width.toMeters())

    fun max(other: Size2d) = Size2d(max(width, other.width), max(height, other.height))
}
