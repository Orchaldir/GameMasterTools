package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.utils.math.RangeInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatisticLimitLayer {
    Race,
    Character,
}

enum class StatisticLimitType {
    Absolute,
    Relative,
    RelativeLowerAndAbsoluteUpper,
    Unmodifiable,
    Undefined,
}

@Serializable
sealed class StatisticLimit {

    fun getType() = when (this) {
        is AbsoluteStatisticLimit -> StatisticLimitType.Absolute
        is RelativeStatisticLimit -> StatisticLimitType.Relative
        is RelativeLowerAndAbsoluteUpperLimit -> StatisticLimitType.RelativeLowerAndAbsoluteUpper
        UnmodifiableStatistic -> StatisticLimitType.Unmodifiable
        UndefinedStatisticLimit -> StatisticLimitType.Undefined
    }

    fun getRelativeRange(base: Int, default: Int) = when (this) {
        is AbsoluteStatisticLimit -> range - base
        is RelativeStatisticLimit -> range
        is RelativeLowerAndAbsoluteUpperLimit -> RangeInt(lower, upper - base)
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
@SerialName("Undefined")
data object UndefinedStatisticLimit : StatisticLimit()

@Serializable
@SerialName("Unchanging")
data object UnmodifiableStatistic : StatisticLimit()