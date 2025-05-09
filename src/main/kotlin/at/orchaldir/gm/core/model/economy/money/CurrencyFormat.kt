package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.checkFactor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.checkDistance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_RADIUS = fromMillimeters(1)
val DEFAULT_RADIUS = fromCentimeters(1)
val MAX_RADIUS = fromCentimeters(10)

val MIN_THICKNESS = fromMillimeters(1)
val DEFAULT_THICKNESS = fromMillimeters(2)
val MAX_THICKNESS = fromMillimeters(10)

val DEFAULT_RIM_FACTOR = fromPercentage(10)
val MAX_RIM_FACTOR = fromPercentage(20)

val MIN_RADIUS_FACTOR = fromPercentage(10)
val DEFAULT_RADIUS_FACTOR = fromPercentage(20)
val MAX_RADIUS_FACTOR = fromPercentage(90)

enum class CurrencyFormatType {
    Undefined,
    Coin,
    HoledCoin,
    BiMetallicCoin,
}

@Serializable
sealed class CurrencyFormat {

    fun getType() = when (this) {
        is UndefinedCurrencyFormat -> CurrencyFormatType.Undefined
        is Coin -> CurrencyFormatType.Coin
        is HoledCoin -> CurrencyFormatType.HoledCoin
        is BiMetallicCoin -> CurrencyFormatType.BiMetallicCoin
    }

    fun contains(id: MaterialId) = when (this) {
        UndefinedCurrencyFormat -> false
        is Coin -> material == id
        is HoledCoin -> material == id
        is BiMetallicCoin -> material == id || innerMaterial == id
    }

    fun getFonts() = when (this) {
        UndefinedCurrencyFormat -> emptySet()
        is Coin -> setOfNotNull(front.font())
        is HoledCoin -> front.getFonts()
        is BiMetallicCoin -> setOfNotNull(front.font())
    }

    fun getMaterials() = when (this) {
        UndefinedCurrencyFormat -> emptySet()
        is Coin -> setOf(material)
        is HoledCoin -> setOf(material)
        is BiMetallicCoin -> setOf(material, innerMaterial)
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedCurrencyFormat : CurrencyFormat()

@Serializable
@SerialName("Coin")
data class Coin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = DEFAULT_RADIUS,
    val thickness: Distance = DEFAULT_THICKNESS,
    val rimFactor: Factor = DEFAULT_RIM_FACTOR,
    val front: CoinSide = BlankCoinSide,
) : CurrencyFormat() {

    init {
        checkRadius(radius)
        checkThickness(thickness)
        checkRimFactor(rimFactor)
    }

    fun calculateInnerShapeRadius(inner: Shape) = calculateInnerRadius(radius, shape, inner)

}

@Serializable
@SerialName("Holed")
data class HoledCoin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = DEFAULT_RADIUS,
    val thickness: Distance = DEFAULT_THICKNESS,
    val rimFactor: Factor = DEFAULT_RIM_FACTOR,
    val holeShape: Shape = Shape.Circle,
    val holeFactor: Factor = DEFAULT_RADIUS_FACTOR,
    val hasHoleRim: Boolean = true,
    val front: HoledCoinSide = HoledCoinSide(),
) : CurrencyFormat() {

    init {
        checkRadius(radius)
        checkThickness(thickness)
        checkRadiusFactor(holeFactor, "hole")
        checkRimFactor(rimFactor)
    }

    fun calculateInnerShapeRadius(other: Shape) = calculateInnerRadius(radius, shape, other)

    fun calculateHoleRadius() = calculateInnerShapeRadius(holeShape) * holeFactor

}

@Serializable
@SerialName("BiMetallic")
data class BiMetallicCoin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = DEFAULT_RADIUS,
    val thickness: Distance = DEFAULT_THICKNESS,
    val rimFactor: Factor = DEFAULT_RIM_FACTOR,
    val innerMaterial: MaterialId = MaterialId(1),
    val innerShape: Shape = Shape.Circle,
    val innerFactor: Factor = DEFAULT_RADIUS_FACTOR,
    val front: CoinSide = BlankCoinSide,
) : CurrencyFormat() {

    init {
        require(material != innerMaterial) { "Outer & inner material are the same!" }
        checkRadius(radius)
        checkThickness(thickness)
        checkRimFactor(rimFactor)
        checkRadiusFactor(innerFactor, "inner")
    }

    fun calculateInnerShapeRadius(other: Shape) = calculateInnerRadius(radius, shape, other)

    fun calculateInnerRadius() = calculateInnerShapeRadius(innerShape) * innerFactor

}

private fun checkRadius(radius: Distance) =
    checkDistance(radius, "radius", MIN_RADIUS, MAX_RADIUS)

private fun checkThickness(thickness: Distance) =
    checkDistance(thickness, "thickness", MIN_THICKNESS, MAX_THICKNESS)

private fun checkRimFactor(factor: Factor) =
    checkFactor(factor, "rim", ZERO, MAX_RIM_FACTOR)

private fun checkRadiusFactor(factor: Factor, label: String) =
    checkFactor(factor, label, MIN_RADIUS_FACTOR, MAX_RADIUS_FACTOR)

private fun calculateInnerRadius(radius: Distance, outer: Shape, inner: Shape): Distance {
    val outerSides = outer.getSides()

    if (outer == Shape.Circle ||
        (outerSides == inner.getSides() && outer.hasCornerAtTop() == inner.hasCornerAtTop())
    ) {
        return radius
    }

    return outer.calculateIncircle(radius, outerSides)
}
