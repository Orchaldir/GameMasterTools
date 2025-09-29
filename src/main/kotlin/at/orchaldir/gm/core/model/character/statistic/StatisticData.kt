package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatisticDataType {
    Attribute,
    DerivedAttribute,
    Skill,
}

@Serializable
sealed class StatisticData {

    fun getType() = when (this) {
        is Attribute -> StatisticDataType.Attribute
        is DerivedAttribute -> StatisticDataType.DerivedAttribute
        is Skill -> StatisticDataType.Skill
    }

    fun isBasedOn(statistic: StatisticId) = baseValue().isBasedOn(statistic)

    fun baseValue() = when (this) {
        is Attribute -> base
        is DerivedAttribute -> base
        is Skill -> base
    }

    fun cost() = when (this) {
        is Attribute -> cost
        is DerivedAttribute -> cost
        is Skill -> cost
    }
}

@Serializable
@SerialName("Attribute")
data class Attribute(
    val base: BaseValue = FixedNumber(0),
    val cost: StatisticCost = UndefinedStatisticCost,
) : StatisticData()

@Serializable
@SerialName("Derived")
data class DerivedAttribute(
    val base: BaseValue = FixedNumber(0),
    val cost: StatisticCost = UndefinedStatisticCost,
    val unit: StatisticUnit = UnitlessStatistic,
) : StatisticData()

@Serializable
@SerialName("Skill")
data class Skill(
    val base: BaseValue = FixedNumber(0),
    val cost: StatisticCost = UndefinedStatisticCost,
) : StatisticData()