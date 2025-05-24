package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
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
    val shape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val radius: Distance = DEFAULT_RADIUS,
    val thickness: Distance = DEFAULT_THICKNESS,
    val rimFactor: Factor = DEFAULT_RIM_FACTOR,
    val front: CoinSide = BlankCoinSide,
) : CurrencyFormat() {

    //fun calculateInnerShapeRadius(inner: CircularShape) = shape.calculateIncircle(radius, inner)

}

@Serializable
@SerialName("Holed")
data class HoledCoin(
    val material: MaterialId = MaterialId(0),
    val shape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val radius: Distance = DEFAULT_RADIUS,
    val thickness: Distance = DEFAULT_THICKNESS,
    val rimFactor: Factor = DEFAULT_RIM_FACTOR,
    val holeShape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val holeFactor: Factor = DEFAULT_RADIUS_FACTOR,
    val hasHoleRim: Boolean = true,
    val front: HoledCoinSide = HoledCoinSide(),
) : CurrencyFormat() {

    //fun calculateInnerShapeRadius(inner: CircularShape) = shape.calculateIncircle(radius, inner)

    //fun calculateHoleRadius() = calculateInnerShapeRadius(holeShape) * holeFactor

}

@Serializable
@SerialName("BiMetallic")
data class BiMetallicCoin(
    val material: MaterialId = MaterialId(0),
    val shape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val radius: Distance = DEFAULT_RADIUS,
    val thickness: Distance = DEFAULT_THICKNESS,
    val rimFactor: Factor = DEFAULT_RIM_FACTOR,
    val innerMaterial: MaterialId = MaterialId(1),
    val innerShape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val innerFactor: Factor = DEFAULT_RADIUS_FACTOR,
    val front: CoinSide = BlankCoinSide,
) : CurrencyFormat() {

    // TODO
    //fun calculateInnerShapeRadius(inner: CircularShape) = shape.calculateIncircle(radius, inner)

    //fun calculateInnerRadius() = calculateInnerShapeRadius(innerShape) * innerFactor

}

