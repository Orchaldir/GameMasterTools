package at.orchaldir.gm.core.model.rpg.statistic

import kotlinx.serialization.Serializable


@Serializable
data class StatisticLimits(
    val attributes: StatisticLimit = UndefinedStatisticLimit,
    val damage: StatisticLimit = UndefinedStatisticLimit,
    val derivedAttributes: StatisticLimit = UndefinedStatisticLimit,
    val saves: StatisticLimit = UndefinedStatisticLimit,
    val skills: StatisticLimit = UndefinedStatisticLimit,
) {

    fun getLimit(type: StatisticDataType) = when (type) {
        StatisticDataType.Attribute -> attributes
        StatisticDataType.Damage -> damage
        StatisticDataType.DerivedAttribute -> derivedAttributes
        StatisticDataType.Save -> saves
        StatisticDataType.Skill -> skills
    }

    fun getLimit(statistic: Statistic) = getLimit(statistic.data.getType())

}
