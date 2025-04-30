package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.name.Name
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class MonthsType {
    Simple,
    Complex,
}

@Serializable
sealed class Months {

    fun getType() = when (this) {
        is SimpleMonths -> MonthsType.Simple
        is ComplexMonths -> MonthsType.Complex
    }

    fun getDaysPerMonth(monthIndex: Int) = when (this) {
        is SimpleMonths -> daysPerMonth
        is ComplexMonths -> months[monthIndex].days
    }

    fun getDaysPerYear() = when (this) {
        is SimpleMonths -> months.size * daysPerMonth
        is ComplexMonths -> months.sumOf { it.days }
    }

    fun getMinDaysPerMonth() = when (this) {
        is SimpleMonths -> daysPerMonth
        is ComplexMonths -> months.minOf { it.days }
    }

    fun getMonth(monthIndex: Int) = when (this) {
        is SimpleMonths -> MonthDefinition(months[monthIndex], daysPerMonth)
        is ComplexMonths -> months[monthIndex]
    }

    fun months() = when (this) {
        is SimpleMonths -> months.map { MonthDefinition(it, daysPerMonth) }
        is ComplexMonths -> months
    }

    fun getSize() = when (this) {
        is SimpleMonths -> months.size
        is ComplexMonths -> months.size
    }
}

@Serializable
@SerialName("Simple")
data class SimpleMonths(
    val daysPerMonth: Int,
    val months: List<Name>,
) : Months()

@Serializable
@SerialName("Complex")
data class ComplexMonths(val months: List<MonthDefinition> = emptyList()) : Months()
