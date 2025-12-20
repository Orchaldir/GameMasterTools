package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume

val SHAPES_WITHOUT_CROSS = RectangularShape.entries - RectangularShape.Cross

private val TEARDROP_HEIGHT = Factor.fromPercentage(70)
private val REVERSE_TEARDROP_HEIGHT = FULL - TEARDROP_HEIGHT

enum class RectangularShape {
    Cross,
    Ellipse,
    Heater,
    RoundedHeater,
    Rectangle,
    RoundedRectangle,
    Teardrop,
    ReverseTeardrop;

    fun isRounded() = when (this) {
        Heater, RoundedHeater, RoundedRectangle, Ellipse, Teardrop, ReverseTeardrop -> true
        else -> false
    }

    fun calculateArea(size: Size2d) = size.width.toMeters() * size.height.toMeters()

    fun calculateVolume(size: Size2d, thickness: Distance) =
        Volume.fromCubicMeters(calculateArea(size) * thickness.toMeters())

    fun calculateCenter(aabb: AABB) = when (this) {
        Heater, RoundedHeater, Rectangle, RoundedRectangle, Ellipse -> aabb.getCenter()
        Cross -> aabb.getPoint(CENTER, CROSS_Y)
        Teardrop -> aabb.getPoint(CENTER, TEARDROP_HEIGHT)
        ReverseTeardrop -> aabb.getPoint(CENTER, REVERSE_TEARDROP_HEIGHT)
    }

    fun calculateInnerSize(size: Size2d, innerWidthFactor: Factor): Size2d {
        val innerWidth = calculateWidth(size.height, innerWidthFactor)
        val innerSize = Size2d(innerWidth, size.height)

        return if (innerSize.width > size.width) {
            val newFactor = size.width / innerSize.width
            val newHeight = innerSize.height * newFactor

            Size2d(size.width, newHeight)
        } else {
            innerSize
        }
    }

    fun calculateWidth(height: Distance, factor: Factor) = height * factor
    fun calculateHeight(width: Distance, factor: Factor) = width / factor
    fun calculateSize(radius: Distance, factor: Factor): Size2d {
        val height = radius * 2
        return Size2d(calculateWidth(height, factor), height)
    }
}