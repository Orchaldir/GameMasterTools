package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

@Serializable
data class Size2i(val width: Distance, val height: Distance) {

    init {
        require(width.value() > 0) { "Width muster be greater 0!" }
        require(height.value() > 0) { "Height muster be greater 0!" }
    }

    companion object {
        fun fromMillimeters(width: Int, height: Int) = Size2i(
            Distance.fromMillimeters(width),
            Distance.fromMillimeters(height),
        )

        fun square(distance: Distance) = Size2i(distance, distance)
    }

    fun toSize2d() = Size2d(width.toMeters(), height.toMeters())

}
