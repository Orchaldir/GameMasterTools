package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CurrencyFormatType {
    Undefined,
    Coin,
}

@Serializable
sealed class CurrencyFormat {

    fun getType() = when (this) {
        is UndefinedCurrencyFormat -> CurrencyFormatType.Undefined
        is Coin -> CurrencyFormatType.Coin
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedCurrencyFormat : CurrencyFormat()

@Serializable
@SerialName("Coin")
data class Coin(
    val material: MaterialId = MaterialId(0),
    val shape: Shape = Circle,
    val radius: Distance = Distance.fromCentimeters(1),
) : CurrencyFormat()
