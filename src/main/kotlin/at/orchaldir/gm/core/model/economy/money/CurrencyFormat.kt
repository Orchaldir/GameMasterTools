package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_RADIUS_FACTOR = fromPercentage(10)
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
    val radius: Distance = Distance.fromCentimeters(1),
) : CurrencyFormat()

@Serializable
@SerialName("Holed")
data class HoledCoin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = Distance.fromCentimeters(1),
    val holeShape: Shape = Shape.Circle,
    val holeFactor: Factor = fromPercentage(20),
) : CurrencyFormat() {

    init {
        checkRadiusFactor(holeFactor, "hole")
    }

}

@Serializable
@SerialName("BiMetallic")
data class BiMetallicCoin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Shape.Circle,
    val radius: Distance = Distance.fromCentimeters(1),
    val innerMaterial: MaterialId = MaterialId(1),
    val innerShape: Shape = Shape.Circle,
    val innerFactor: Factor = fromPercentage(20),
) : CurrencyFormat() {

    init {
        require(material != innerMaterial) { "Outer & inner material are the same!" }
        checkRadiusFactor(innerFactor, "inner")
    }

}

private fun checkRadiusFactor(factor: Factor, label: String) {
    require(factor >= MIN_RADIUS_FACTOR) { "The $label factor is too small!" }
    require(factor <= MAX_RADIUS_FACTOR) { "The $label factor is too large!" }
}
