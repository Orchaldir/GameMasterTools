package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class DaysType {
    DayOfTheMonth,
    Weekdays,
}

@Serializable
sealed class Days {

    fun getType() = when (this) {
        DayOfTheMonth -> DaysType.DayOfTheMonth
        is Weekdays -> DaysType.Weekdays
    }
}

@Serializable
@SerialName("DayOfTheMonth")
data object DayOfTheMonth : Days()

@Serializable
@SerialName("Weekdays")
data class Weekdays(val weekDays: List<WeekDay> = emptyList()) : Days()
