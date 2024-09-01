package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.calendar.Weekdays
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RelativeDateType {
    FixedDayInYear,
    WeekdayInMonth,
}

@Serializable
sealed class RelativeDate {

    fun getType() = when (this) {
        is FixedDayInYear -> RelativeDateType.FixedDayInYear
        is WeekdayInMonth -> RelativeDateType.WeekdayInMonth
    }

    abstract fun resolve(calendar: Calendar): String
}

@Serializable
@SerialName("FixedDayInYear")
data class FixedDayInYear(val dayIndex: Int) : RelativeDate() {
    override fun resolve(calendar: Calendar): String {
        val (monthIndex, dayIndex) = calendar.resolveDayAndMonth(dayIndex)
        val month = calendar.months[monthIndex]
        val day = dayIndex + 1

        return "${day}.${month.name}"
    }
}

@Serializable
@SerialName("WeekdayInMonth")
data class WeekdayInMonth(val weekdayIndex: Int, val weekInMonthIndex: Int, val monthIndex: Int) : RelativeDate() {
    override fun resolve(calendar: Calendar): String {
        when (calendar.days) {
            DayOfTheMonth -> error("WeekdayInMonth doesn't support DayOfTheMonth!")
            is Weekdays -> {
                val month = calendar.months[monthIndex]
                val count = weekInMonthIndex + 1
                val weekday = calendar.days.weekDays[weekdayIndex]

                return "${count}.${weekday.name} of ${month.name}"
            }
        }

    }
}
