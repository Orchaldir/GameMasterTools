package at.orchaldir.gm.core.model.rpg.statistic

data class StatisticLimits(
    val attributes: StatisticLimit = UndefinedStatisticLimit,
    val derivedAttributes: StatisticLimit = UndefinedStatisticLimit,
    val saves: StatisticLimit = UndefinedStatisticLimit,
    val skills: StatisticLimit = UndefinedStatisticLimit,
)
