package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
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

    fun isRounded() = when (this) {
        is UsingCircularShape -> shape.isRounded()
        is UsingRectangularShape -> shape.isRounded()
    }

    fun calculateVolume(radius: Distance, thickness: Distance) = when (this) {
        is UsingCircularShape -> shape.calculateVolumeOfPrism(radius, thickness)
        is UsingRectangularShape -> shape.calculateVolumeOfPrism(shape.calculateSize(radius, widthFactor), thickness)
    }

    fun calculateVolume(size: Size2d, thickness: Distance) = when (this) {
        is UsingCircularShape -> shape.calculateVolumeOfPrism(size.innerRadius(), thickness)
        is UsingRectangularShape -> shape.calculateVolumeOfPrism(size, thickness)
    }

    abstract fun calculateAabb(center: Point2d, radius: Distance): AABB
    abstract fun calculateSizeFromWidth(width: Distance): Size2d
    abstract fun calculateIncircle(radius: Distance, inner: ComplexShape): Size2d
    abstract fun calculateInnerAabb(aabb: AABB, inner: ComplexShape, factor: Factor): AABB
}

@Serializable
@SerialName("Circular")
data class UsingCircularShape(
    val shape: CircularShape = CircularShape.Circle,
) : ComplexShape() {

    override fun calculateAabb(center: Point2d, radius: Distance) = AABB.fromRadius(center, radius)
    override fun calculateSizeFromWidth(width: Distance) = Size2d.square(width)

    override fun calculateIncircle(radius: Distance, inner: ComplexShape) = when (inner) {
        is UsingCircularShape -> {
            val innerRadius = shape.calculateIncircle(radius, inner.shape)
            Size2d.square(innerRadius * 2)
        }

        is UsingRectangularShape -> {
            val innerRadius = shape.calculateIncircle(radius, CircularShape.Circle)
            val halfHeight = innerRadius / sqrt(inner.widthFactor.toNumber().pow(2) + 1.0f)
            val height = halfHeight * 2.0f

            Size2d(inner.shape.calculateWidth(height, inner.widthFactor), height)
        }
    }

    override fun calculateInnerAabb(
        aabb: AABB,
        inner: ComplexShape,
        factor: Factor,
    ): AABB {
        val innerSize = calculateIncircle(aabb.getInnerRadius(), inner) * factor

        return AABB.fromCenter(aabb.getCenter(), innerSize)
    }

    override fun toString() = shape.toString()

}

@Serializable
@SerialName("Rectangular")
data class UsingRectangularShape(
    val shape: RectangularShape = RectangularShape.Rectangle,
    val widthFactor: Factor = Factor.fromPercentage(50),
) : ComplexShape() {

    override fun calculateAabb(center: Point2d, radius: Distance) =
        AABB.fromRadii(center, shape.calculateWidth(radius, widthFactor), radius)

    override fun calculateSizeFromWidth(width: Distance) =
        Size2d(width, shape.calculateHeight(width, widthFactor))

    override fun calculateIncircle(radius: Distance, inner: ComplexShape): Size2d {
        val size = shape.calculateSize(radius * 2, widthFactor)

        return when (inner) {
            is UsingCircularShape -> Size2d.square(size.minSize())
            is UsingRectangularShape -> shape.calculateInnerSize(size, inner.widthFactor)
        }
    }

    override fun calculateInnerAabb(
        aabb: AABB,
        inner: ComplexShape,
        factor: Factor,
    ) = AABB.fromCenter(
        shape.calculateCenter(aabb),
        calculateIncircle(aabb.getInnerRadius(), inner) * factor,
    )

    override fun toString() = shape.toString()

}
