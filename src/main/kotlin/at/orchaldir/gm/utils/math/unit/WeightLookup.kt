package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WeightLookupType {
    Calculated,
    UserDefined,
}

@Serializable
sealed class WeightLookup {
    fun getType() = when (this) {
        is CalculatedWeight -> WeightLookupType.Calculated
        is UserDefinedWeight -> WeightLookupType.UserDefined
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedWeight : WeightLookup()

@Serializable
@SerialName("User")
data class UserDefinedWeight(
    val weight: Weight,
) : WeightLookup()

