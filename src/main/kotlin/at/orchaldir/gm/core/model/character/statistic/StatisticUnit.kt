package at.orchaldir.gm.core.model.character.statistic

import at.orchaldir.gm.core.model.util.name.NotEmptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatisticUnitType {
    Unitless,
    Suffix,
}

@Serializable
sealed class StatisticUnit {

    fun getType() = when (this) {
        is SuffixedStatisticUnit -> StatisticUnitType.Suffix
        UnitlessStatistic -> StatisticUnitType.Unitless
    }

    fun calculate(level: Int) = when (this) {
        is SuffixedStatisticUnit -> "$level $suffix"
        UnitlessStatistic -> level.toString()
    }
}

@Serializable
@SerialName("Suffix")
data class SuffixedStatisticUnit(
    val suffix: NotEmptyString,
) : StatisticUnit()



@Serializable
@SerialName("Unitless")
data object UnitlessStatistic : StatisticUnit()