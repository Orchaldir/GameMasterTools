package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Size2d(val width: Float, val height: Float) {

    init {
        require(width > 0.0) { "Width muster be greater 0!" }
        require(height > 0.0) { "Height muster be greater 0!" }
    }

    companion object {
        fun square(size: Float) = Size2d(size, size)

        fun square(distance: Distance) = square(distance.value)
    }

    operator fun div(value: Float) = Size2d(width / value, height / value)

}
