package at.orchaldir.gm.core.model.holiday

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RelativeDateType {
    FixedDayInYear,
    WeekdayInMonth,
}

@Serializable
sealed class RelativeDate

@Serializable
@SerialName("FixedDayInYear")
data class FixedDayInYear(val dayIndex: Int) : RelativeDate()

@Serializable
@SerialName("WeekdayInMonth")
data class WeekdayInMonth(val weekdayIndex: Int, val weekInMonthIndex: Int) : RelativeDate()
