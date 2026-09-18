package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.WeightBasedOnAppearance
import at.orchaldir.gm.utils.math.unit.WeightBasedOnType
import at.orchaldir.gm.utils.math.unit.WeightLookupType
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

    fun validate(
        label: String,
        min: Price,
        max: Price,
        allowedTypes: Collection<PriceLookupType> = PriceLookupType.entries,
    ) {
        require(allowedTypes.contains(getType())) {
            "Invalid type ${getType()} for price lookup!"
        }

        when (this) {
            PriceBasedOnAppearance -> doNothing()
            PriceBasedOnType -> doNothing()
            UndefinedPrice -> doNothing()
            is UserDefinedPrice -> price.validate(label, min, max)
        }
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

