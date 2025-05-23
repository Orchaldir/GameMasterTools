package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
}

@Serializable
@SerialName("Circular")
data class UsingCircularShape(
    val shape: CircularShape = CircularShape.Circle,
) : ComplexShape()

@Serializable
@SerialName("Rectangular")
data class UsingRectangularShape(
    val shape: RectangularShape = RectangularShape.Rectangle,
    val factor: Factor = Factor.fromPercentage(50),
) : ComplexShape() {

    fun calculateWidth(height: Distance) = height * factor

}
