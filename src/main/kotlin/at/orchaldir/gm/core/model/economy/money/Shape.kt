package at.orchaldir.gm.core.model.economy.money

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ShapeType {
    Circle,
    Triangle,
    Square,
    RegularPolygon,
}

@Serializable
sealed class Shape {

    fun getType() = when (this) {
        is Circle -> ShapeType.Circle
        is Triangle -> ShapeType.Triangle
        is Square -> ShapeType.Square
        is RegularPolygon -> ShapeType.RegularPolygon
    }
}

@Serializable
@SerialName("Circle")
data object Circle : Shape()

@Serializable
@SerialName("Triangle")
data class Triangle(
    val rounded: Boolean,
    val cornerTop: Boolean,
) : Shape()

@Serializable
@SerialName("Square")
data class Square(
    val rounded: Boolean,
    val cornerTop: Boolean,
) : Shape()

@Serializable
@SerialName("RegularPolygon")
data class RegularPolygon(
    val sides: Int,
    val cornerTop: Boolean,
    val scalloped: Boolean,
) : Shape() {

    init {
        require(sides > 2) { "Regular Polygon has too few sides!" }
    }

}