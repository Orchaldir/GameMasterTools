package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

val MIN_RECTANGULAR_FACTOR = Factor.fromPercentage(10)
val MAX_RECTANGULAR_FACTOR = Factor.fromPercentage(1000)

enum class ComplexShapeType {
    Circular,
    Rectangular,
}

@Serializable
sealed class ComplexShape {

    fun getType() = when (this) {
        is UsingCircularShape -> ComplexShapeType.Circular
        is UsingRectangularShape -> ComplexShapeType.Rectangular
    }

    abstract fun calculateIncircle(radius: Distance, inner: ComplexShape): Size2d
}

@Serializable
@SerialName("Circular")
data class UsingCircularShape(
    val shape: CircularShape = CircularShape.Circle,
) : ComplexShape() {

    override fun calculateIncircle(radius: Distance, inner: ComplexShape) = when (inner) {
        is UsingCircularShape -> {
            val innerRadius = shape.calculateIncircle(radius, inner.shape)
            Size2d.fromDiagonalRadius(innerRadius)
        }

        is UsingRectangularShape -> {
            val innerRadius = shape.calculateIncircle(radius, CircularShape.Circle)
            val halfHeight = innerRadius / sqrt(inner.factor.toNumber().pow(2) + 1.0f)
            val height = halfHeight * 2.0f

            Size2d(inner.shape.calculateWidth(height, inner.factor), height)
        }
    }

}

@Serializable
@SerialName("Rectangular")
data class UsingRectangularShape(
    val shape: RectangularShape = RectangularShape.Rectangle,
    val factor: Factor = Factor.fromPercentage(50),
) : ComplexShape() {

    override fun calculateIncircle(radius: Distance, inner: ComplexShape): Size2d {
        val size = Size2d.square(radius * 2.0f)

        return when (inner) {
            is UsingCircularShape -> Size2d.square(size.minSize())
            is UsingRectangularShape -> shape.calculateIncircle(size, inner.factor)
        }
    }

}
