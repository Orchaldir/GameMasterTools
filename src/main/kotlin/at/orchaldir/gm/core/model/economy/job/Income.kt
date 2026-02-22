package at.orchaldir.gm.core.model.economy.job

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class IncomeType {
    Undefined,
    Salary,
    StandardOfLiving;

    fun getValidTypes() = when (this) {
        Undefined -> IncomeType.entries.toList()
        Salary -> listOf(Undefined, Salary)
        StandardOfLiving -> listOf(Undefined, StandardOfLiving)
    }
}

@Serializable
sealed class Income {

    fun getType() = when (this) {
        is UndefinedIncome -> IncomeType.Undefined
        is AffordableStandardOfLiving -> IncomeType.StandardOfLiving
        is Salary -> IncomeType.Salary
    }

    fun hasStandard(id: StandardOfLivingId) = when (this) {
        is AffordableStandardOfLiving -> standard == id
        else -> false
    }

    fun sortValue(state: State) = when (this) {
        UndefinedIncome -> 0
        is AffordableStandardOfLiving -> state.config.economy.getStandardOfLiving(standard).maxYearlyIncome.value
        is Salary -> yearlySalary.value
    }

    fun validate(state: State) {
        if (this is AffordableStandardOfLiving) {
            state.config.economy.requireStandardOfLiving(standard)
        }
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedIncome : Income()

@Serializable
@SerialName("SoL")
data class AffordableStandardOfLiving(
    val standard: StandardOfLivingId,
) : Income()

@Serializable
@SerialName("Salary")
data class Salary(
    val yearlySalary: Price,
) : Income()

