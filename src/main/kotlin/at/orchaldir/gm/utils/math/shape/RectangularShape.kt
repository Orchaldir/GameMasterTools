package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance

enum class RectangularShape {
    Rectangle,
    Teardrop,
    ReverseTeardrop;

    fun isRounded() = when (this) {
        Teardrop, ReverseTeardrop -> true
        else -> false
    }

    fun calculateArea(size: Size2d) = size.width.toMeters() * size.height.toMeters()

    fun calculateVolume(size: Size2d, thickness: Distance) =
        calculateArea(size) * thickness.toMeters()

    fun calculateIncircle(size: Size2d, innerFactor: Factor): Size2d {
        val innerSize = Size2d(size.height * innerFactor, size.height)

        return if (innerSize.width > size.width) {
            val newFactor = size.width / innerSize.width
            val newHeight = innerSize.height * newFactor

            Size2d(size.width, newHeight)
        } else {
            innerSize
        }
    }

    fun getSides() = when (this) {
        Teardrop, ReverseTeardrop -> 0
        Rectangle -> 4
    }
}