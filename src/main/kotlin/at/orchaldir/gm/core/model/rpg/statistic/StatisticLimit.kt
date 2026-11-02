package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.RangeInt
import at.orchaldir.gm.utils.math.RoundingType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

enum class StatisticLimitLayer {
    Race,
    Character,
}

enum class StatisticLimitType {
    Absolute,
    Relative,
    RelativeLowerAndAbsoluteUpper,
    RelativeWithFactor,
    Unmodifiable,
    Undefined,
}

@Serializable
sealed class StatisticLimit {

    fun getType() = when (this) {
        is AbsoluteStatisticLimit -> StatisticLimitType.Absolute
        is RelativeStatisticLimit -> StatisticLimitType.Relative
        is RelativeLowerAndAbsoluteUpperLimit -> StatisticLimitType.RelativeLowerAndAbsoluteUpper
        is RelativeWithFactorStatisticLimit -> StatisticLimitType.RelativeWithFactor
        UnmodifiableStatistic -> StatisticLimitType.Unmodifiable
        UndefinedStatisticLimit -> StatisticLimitType.Undefined
    }

    fun getRelativeRange(base: Int, default: Int) = when (this) {
        is AbsoluteStatisticLimit -> range - base
        is RelativeStatisticLimit -> range
        is RelativeLowerAndAbsoluteUpperLimit -> if (base > upper) {
            val relativeMax = upper - base
            RangeInt(relativeMax + lower, relativeMax)
        } else {
            RangeInt(lower, upper - base)
        }
        is RelativeWithFactorStatisticLimit -> {
            val offset = factor.apply(base.absoluteValue, rounding)

            RangeInt(-offset, offset)
        }
        UnmodifiableStatistic -> RangeInt(0, 0)
        UndefinedStatisticLimit -> RangeInt(-default, +default)
    }
}

@Serializable
@SerialName("Absolute")
data class AbsoluteStatisticLimit(
    val range: RangeInt,
) : StatisticLimit()

@Serializable
@SerialName("Relative")
data class RelativeStatisticLimit(
    val range: RangeInt,
) : StatisticLimit()

@Serializable
@SerialName("RelativeAndAbsolute")
data class RelativeLowerAndAbsoluteUpperLimit(
    val lower: Int,
    val upper: Int,
) : StatisticLimit()

@Serializable
@SerialName("RelativeWithFactor")
data class RelativeWithFactorStatisticLimit(
    val factor: Factor,
    val rounding: RoundingType = RoundingType.RoundUp,
) : StatisticLimit()

@Serializable
@SerialName("Undefined")
data object UndefinedStatisticLimit : StatisticLimit()

@Serializable
@SerialName("Unchanging")
data object UnmodifiableStatistic : StatisticLimit()