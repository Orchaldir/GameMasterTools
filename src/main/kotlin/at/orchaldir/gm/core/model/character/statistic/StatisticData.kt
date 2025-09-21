package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatisticDataType {
    Attribute,
    Skill,
}

@Serializable
sealed class StatisticData {

    fun getType() = when (this) {
        is Attribute -> StatisticDataType.Attribute
        is Skill -> StatisticDataType.Skill
    }
}

@Serializable
@SerialName("Attribute")
data class Attribute(
    val base: BaseValue = FixedNumber(0),
) : StatisticData()

@Serializable
@SerialName("Skill")
data class Skill(
    val base: BaseValue = FixedNumber(0),
) : StatisticData()