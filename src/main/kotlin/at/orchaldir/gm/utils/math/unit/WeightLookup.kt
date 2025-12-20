package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WeightLookupType {
    Calculated,
    Fixed,
    Undefined,
}

@Serializable
sealed class WeightLookup {
    fun getType() = when (this) {
        is CalculatedWeight -> WeightLookupType.Calculated
        is FixedWeight -> WeightLookupType.Fixed
        UndefinedWeight -> WeightLookupType.Undefined
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedWeight : WeightLookup()

@Serializable
@SerialName("Fixed")
data class FixedWeight(
    val weight: Weight,
) : WeightLookup()

@Serializable
@SerialName("Undefined")
data object UndefinedWeight : WeightLookup()
