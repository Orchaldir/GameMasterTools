package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BaseValueType {
    FixedNumber,
    BasedOnStatistic,
}

@Serializable
sealed class BaseValue {

    fun getType() = when (this) {
        is FixedNumber -> BaseValueType.FixedNumber
        is BasedOnStatistic -> BaseValueType.BasedOnStatistic
    }

    fun isBasedOn(statistic: StatisticId) = when(this) {
        is BasedOnStatistic -> this.statistic == statistic
        is FixedNumber -> false
    }
}

@Serializable
@SerialName("Fixed")
data class FixedNumber(
    val default: Int,
) : BaseValue()

@Serializable
@SerialName("Statistic")
data class BasedOnStatistic(
    val statistic: StatisticId,
    val offset: Int = 0,
) : BaseValue()