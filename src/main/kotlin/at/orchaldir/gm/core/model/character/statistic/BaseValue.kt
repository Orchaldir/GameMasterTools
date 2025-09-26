package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BaseValueType {
    BasedOnStatistic,
    Division,
    FixedNumber,
    Product,
    Sum,
}

@Serializable
sealed class BaseValue {

    fun getType() = when (this) {
        is BasedOnStatistic -> BaseValueType.BasedOnStatistic
        is DivisionOfValues -> BaseValueType.Division
        is FixedNumber -> BaseValueType.FixedNumber
        is ProductOfValues -> BaseValueType.Product
        is SumOfValues -> BaseValueType.Sum
    }

    fun isBasedOn(statistic: StatisticId) = when (this) {
        is BasedOnStatistic -> this.statistic == statistic
        else -> false
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

@Serializable
@SerialName("Sum")
data class SumOfValues(
    val values: List<BaseValue>,
) : BaseValue()

@Serializable
@SerialName("Product")
data class ProductOfValues(
    val values: List<BaseValue>,
) : BaseValue()

@Serializable
@SerialName("Division")
data class DivisionOfValues(
    val dividend: BaseValue,
    val divisor: BaseValue,
) : BaseValue()