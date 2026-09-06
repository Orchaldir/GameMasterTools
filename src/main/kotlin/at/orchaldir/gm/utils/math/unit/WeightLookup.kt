package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WeightLookupType {
    Calculated,
    Type,
    Undefined,
    UserDefined,
}

@Serializable
sealed class WeightLookup {
    fun getType() = when (this) {
        CalculatedWeight -> WeightLookupType.Calculated
        is UserDefinedWeight -> WeightLookupType.UserDefined
        UndefinedWeight -> WeightLookupType.Undefined
        WeightBasedOnType -> WeightLookupType.Type
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedWeight : WeightLookup()

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

