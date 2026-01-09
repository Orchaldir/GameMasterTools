package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RangeType {
    FixedHalfAndMax,
    StatisticBasedHalfAndMax,
    Undefined,
}

@Serializable
sealed class Range {

    fun getType() = when (this) {
        is FixedHalfAndMaxRange -> RangeType.FixedHalfAndMax
        is StatisticBasedHalfAndMaxRange -> RangeType.StatisticBasedHalfAndMax
        is UndefinedRange -> RangeType.Undefined
    }
}

@Serializable
@SerialName("FixedHalfAndMax")
data class FixedHalfAndMaxRange(
    val min: Int,
    val max: Int,
) : Range()

@Serializable
@SerialName("StatisticBasedHalfAndMax")
data class StatisticBasedHalfAndMaxRange(
    val statistic: StatisticId,
    val min: Factor,
    val max: Factor,
) : Range()

@Serializable
@SerialName("Undefined")
data object UndefinedRange : Range()
