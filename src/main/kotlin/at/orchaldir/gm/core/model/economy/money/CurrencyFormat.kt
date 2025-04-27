package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_RADIUS = fromMillimeters(1)
val DEFAULT_RADIUS = fromCentimeters(1)
val MAX_RADIUS = fromCentimeters(10)

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
) : CurrencyFormat() {

    init {
        checkRadius(radius)
    }

}

@Serializable
@SerialName("Holed")
data class HoledCoin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = DEFAULT_RADIUS,
    val holeShape: Shape = Shape.Circle,
    val holeFactor: Factor = DEFAULT_RADIUS_FACTOR,
) : CurrencyFormat() {

    init {
        checkRadius(radius)
        checkRadiusFactor(holeFactor, "hole")
    }

    fun calculateHoleRadius() = calculateInnerRadius(radius, shape, holeShape) * holeFactor

}

@Serializable
@SerialName("BiMetallic")
data class BiMetallicCoin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = DEFAULT_RADIUS,
    val innerMaterial: MaterialId = MaterialId(1),
    val innerShape: Shape = Shape.Circle,
    val innerFactor: Factor = DEFAULT_RADIUS_FACTOR,
) : CurrencyFormat() {

    init {
        require(material != innerMaterial) { "Outer & inner material are the same!" }
        checkRadius(radius)
        checkRadiusFactor(innerFactor, "inner")
    }

    fun calculateInnerRadius() = calculateInnerRadius(radius, shape, innerShape) * innerFactor

}

private fun checkRadius(radius: Distance) {
    require(radius >= MIN_RADIUS) { "The radius is too small!" }
    require(radius <= MAX_RADIUS) { "The radius factor is too large!" }
}

private fun checkRadiusFactor(factor: Factor, label: String) {
    require(factor >= MIN_RADIUS_FACTOR) { "The $label factor is too small!" }
    require(factor <= MAX_RADIUS_FACTOR) { "The $label factor is too large!" }
}

private fun calculateInnerRadius(radius: Distance, outer: Shape, inner: Shape): Distance {
    val outerSides = outer.getSides()

    if (outer == Shape.Circle ||
        (outerSides == inner.getSides() && outer.hasCornerAtTop() == inner.hasCornerAtTop())
    ) {
        return radius
    }

    return calculateIncircle(radius, outerSides)
}
