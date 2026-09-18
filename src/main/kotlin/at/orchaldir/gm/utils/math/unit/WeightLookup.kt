package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOWED_WEIGHT_LOOKUP_TYPES_FOR_TYPES = listOf(
    WeightLookupType.Undefined,
    WeightLookupType.UserDefined,
)

enum class WeightLookupType {
    Undefined,
    Appearance,
    Type,
    UserDefined,
}

@Serializable
sealed class WeightLookup {
    fun getType() = when (this) {
        WeightBasedOnAppearance -> WeightLookupType.Appearance
        is UserDefinedWeight -> WeightLookupType.UserDefined
        UndefinedWeight -> WeightLookupType.Undefined
        WeightBasedOnType -> WeightLookupType.Type
    }

    fun validate(
        label: String,
        min: Weight,
        max: Weight,
        allowedTypes: Collection<WeightLookupType> = WeightLookupType.entries,
    ) {
        require(allowedTypes.contains(getType())) {
            "Invalid type ${getType()} for weight lookup!"
        }

        when (this) {
            WeightBasedOnAppearance -> doNothing()
            is UserDefinedWeight -> weight.validate(label, min, max)
            UndefinedWeight -> doNothing()
            WeightBasedOnType -> doNothing()
        }
    }
}

@Serializable
@SerialName("Appearance")
data object WeightBasedOnAppearance : WeightLookup()

@Serializable
@SerialName("Type")
data object WeightBasedOnType : WeightLookup()

@Serializable
@SerialName("Undefined")
data object UndefinedWeight : WeightLookup()

@Serializable
@SerialName("User")
data class UserDefinedWeight(
    val weight: Weight,
) : WeightLookup()

