package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WeightLookupType {
    Calculated,
    Fixed,
}

@Serializable
sealed class WeightLookup {
    fun getType() = when (this) {
        is CalculatedWeight -> WeightLookupType.Calculated
        is FixedWeight -> WeightLookupType.Fixed
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

