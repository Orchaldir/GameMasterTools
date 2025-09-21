package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatisticDataType {
    Attribute,
}

@Serializable
sealed class StatisticData {

    fun getType() = when (this) {
        is Attribute -> StatisticDataType.Attribute
    }
}

@Serializable
@SerialName("Attribute")
data class Attribute(
    val base: BaseValue = FixedNumber(0),
) : StatisticData()