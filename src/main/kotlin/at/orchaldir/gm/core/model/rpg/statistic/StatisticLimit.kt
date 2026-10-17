package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.utils.math.RangeInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatisticLimitType {
    Undefined,
    Absolute,
    Relative,
}

@Serializable
sealed class StatisticLimit {

    fun getType() = when (this) {
        is AbsoluteStatisticLimit -> StatisticLimitType.Absolute
        is RelativeStatisticLimit -> StatisticLimitType.Relative
        UndefinedStatisticLimit -> StatisticLimitType.Undefined
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
@SerialName("Undefined")
data object UndefinedStatisticLimit : StatisticLimit()