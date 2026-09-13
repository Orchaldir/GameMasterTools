package at.orchaldir.gm.core.model.economy.money

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOWED_PRICE_LOOKUP_TYPES_FOR_TYPES = listOf(
    PriceLookupType.Undefined,
    PriceLookupType.UserDefined,
)

enum class PriceLookupType {
    Undefined,
    Appearance,
    Type,
    UserDefined,
}

@Serializable
sealed class PriceLookup {
    fun getType() = when (this) {
        PriceBasedOnAppearance -> PriceLookupType.Appearance
        PriceBasedOnType -> PriceLookupType.Type
        UndefinedPrice -> PriceLookupType.Undefined
        is UserDefinedPrice -> PriceLookupType.UserDefined
    }
}

@Serializable
@SerialName("Appearance")
data object PriceBasedOnAppearance : PriceLookup()

@Serializable
@SerialName("Type")
data object PriceBasedOnType : PriceLookup()

@Serializable
@SerialName("Undefined")
data object UndefinedPrice : PriceLookup()

@Serializable
@SerialName("User")
data class UserDefinedPrice(
    val price: Price,
) : PriceLookup()

