package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RangeType {
    FixedHalfAndMax,
    MusclePoweredHalfAndMax,
    StatisticBasedHalfAndMax,
    Undefined,
}

@Serializable
sealed class Range {

    fun getType() = when (this) {
        is FixedHalfAndMaxRange -> RangeType.FixedHalfAndMax
        is MusclePoweredHalfAndMaxRange -> RangeType.MusclePoweredHalfAndMax
        is StatisticBasedHalfAndMaxRange -> RangeType.StatisticBasedHalfAndMax
        is UndefinedRange -> RangeType.Undefined
    }

    fun contains(other: StatisticId) = when (this) {
        is StatisticBasedHalfAndMaxRange -> statistic == other
        else -> false
    }

    operator fun times(factor: Factor) = when (this) {
        is FixedHalfAndMaxRange -> FixedHalfAndMaxRange(
            factor.apply(half),
            factor.apply(max),
        )

        is MusclePoweredHalfAndMaxRange -> MusclePoweredHalfAndMaxRange(
            half * factor,
            max * factor,
        )

        is StatisticBasedHalfAndMaxRange -> StatisticBasedHalfAndMaxRange(
            statistic,
            half * factor,
            max * factor,
        )

        UndefinedRange -> this
    }
}

@Serializable
@SerialName("FixedHalfAndMax")
data class FixedHalfAndMaxRange(
    val half: Int,
    val max: Int,
) : Range()

@Serializable
@SerialName("MusclePoweredHalfAndMax")
data class MusclePoweredHalfAndMaxRange(
    val half: Factor,
    val max: Factor,
) : Range()

@Serializable
@SerialName("StatisticBasedHalfAndMax")
data class StatisticBasedHalfAndMaxRange(
    val statistic: StatisticId,
    val half: Factor,
    val max: Factor,
) : Range()

@Serializable
@SerialName("Undefined")
data object UndefinedRange : Range()
