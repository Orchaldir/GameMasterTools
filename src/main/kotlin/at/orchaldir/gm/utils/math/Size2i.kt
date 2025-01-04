package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Size2i(val width: Int, val height: Int) {

    constructor(width: Distance, height: Distance) : this(width.millimeters, height.millimeters)

    init {
        require(width > 0) { "Width muster be greater 0!" }
        require(height > 0) { "Height muster be greater 0!" }
    }

    companion object {
        fun square(millimeters: Int) = Size2i(millimeters, millimeters)

        fun square(distance: Distance) = square(distance.millimeters)
    }

}
