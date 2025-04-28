package at.orchaldir.gm.core.model.economy.job

import at.orchaldir.gm.core.model.economy.money.Price
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class IncomeType {
    Undefined,
    Salary,
}

@Serializable
sealed class Income {

    fun getType() = when (this) {
        is UndefinedIncome -> IncomeType.Undefined
        is Salary -> IncomeType.Salary
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedIncome : Income()

@Serializable
@SerialName("Salary")
data class Salary(
    val salary: Price,
) : Income()
