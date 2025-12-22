package at.orchaldir.gm.core.model.economy.money

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PriceLookupType {
    Calculated,
    Fixed,
}

@Serializable
sealed class PriceLookup {
    fun getType() = when (this) {
        is CalculatedPrice -> PriceLookupType.Calculated
        is FixedPrice -> PriceLookupType.Fixed
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedPrice : PriceLookup()

@Serializable
@SerialName("Fixed")
data class FixedPrice(
    val price: Price,
) : PriceLookup()

