package at.orchaldir.gm.core.model.economy.money

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PriceLookupType {
    Calculated,
    UserDefined,
}

@Serializable
sealed class PriceLookup {
    fun getType() = when (this) {
        is CalculatedPrice -> PriceLookupType.Calculated
        is UserDefinedPrice -> PriceLookupType.UserDefined
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedPrice : PriceLookup()

@Serializable
@SerialName("User")
data class UserDefinedPrice(
    val price: Price,
) : PriceLookup()

