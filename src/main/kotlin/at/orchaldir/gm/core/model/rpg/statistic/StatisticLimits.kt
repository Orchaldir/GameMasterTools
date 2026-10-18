package at.orchaldir.gm.core.model.rpg.statistic

import kotlinx.serialization.Serializable


@Serializable
data class StatisticLimits(
    val attributes: StatisticLimit = UndefinedStatisticLimit,
    val derivedAttributes: StatisticLimit = UndefinedStatisticLimit,
    val saves: StatisticLimit = UndefinedStatisticLimit,
    val skills: StatisticLimit = UndefinedStatisticLimit,
)
