package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.utils.math.unit.WeightLookupType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOWED_PRICE_LOOKUP_TYPES_FOR_TYPES = listOf(
    PriceLookupType.Undefined,
    PriceLookupType.UserDefined,
)

enum class PriceLookupType {
    Undefined,
    Calculated,
    Type,
    UserDefined,
}

@Serializable
sealed class PriceLookup {
    fun getType() = when (this) {
        CalculatedPrice -> PriceLookupType.Calculated
        PriceBasedOnType -> PriceLookupType.Type
        UndefinedPrice -> PriceLookupType.Undefined
        is UserDefinedPrice -> PriceLookupType.UserDefined
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedPrice : PriceLookup()

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

