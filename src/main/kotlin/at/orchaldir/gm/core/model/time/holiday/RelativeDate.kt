package at.orchaldir.gm.core.model.time.holiday

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.time.date.DisplayDay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RelativeDateType {
    DayInMonth,
    DayInYear,
    WeekdayInMonth,
}

@Serializable
sealed class RelativeDate {

    fun getType() = when (this) {
        is DayInMonth -> RelativeDateType.DayInMonth
        is DayInYear -> RelativeDateType.DayInYear
        is WeekdayInMonth -> RelativeDateType.WeekdayInMonth
    }

    abstract fun display(calendar: Calendar): String
    abstract fun isOn(calendar: Calendar, displayDay: DisplayDay): Boolean
}

@Serializable
@SerialName("DayInMonth")
data class DayInMonth(val dayIndex: Int) : RelativeDate() {
    override fun display(calendar: Calendar): String {
        val day = dayIndex + 1

        return "$day of each Month"
    }

    override fun isOn(calendar: Calendar, displayDay: DisplayDay) = dayIndex == displayDay.dayIndex
}

@Serializable
@SerialName("DayInYear")
data class DayInYear(val dayIndex: Int, val monthIndex: Int) : RelativeDate() {
    override fun display(calendar: Calendar): String {
        val month = calendar.months.getMonth(monthIndex)
        val day = dayIndex + 1

        return "${day}.${month.name.text}"
    }

    override fun isOn(calendar: Calendar, displayDay: DisplayDay) =
        monthIndex == displayDay.month.monthIndex && dayIndex == displayDay.dayIndex
}

@Serializable
@SerialName("WeekdayInMonth")
data class WeekdayInMonth(val weekdayIndex: Int, val weekInMonthIndex: Int, val monthIndex: Int) : RelativeDate() {
    override fun display(calendar: Calendar): String {
        when (calendar.days) {
            DayOfTheMonth -> error("WeekdayInMonth doesn't support DayOfTheMonth!")
            is Weekdays -> {
                val month = calendar.months.getMonth(monthIndex)
                val count = weekInMonthIndex + 1
                val weekday = calendar.days.weekDays[weekdayIndex]

                return "${count}.${weekday.name.text} of ${month.name.text}"
            }
        }

    }

    override fun isOn(calendar: Calendar, displayDay: DisplayDay): Boolean {
        if (monthIndex != displayDay.month.monthIndex) {
            return false
        } else if (weekdayIndex != displayDay.weekdayIndex) {
            return false
        }

        return when (calendar.days) {
            DayOfTheMonth -> error("WeekdayInMonth.isOn() doesn't support DayOfTheMonth!")
            is Weekdays -> {
                val displayWeekInMonthIndex = displayDay.dayIndex / calendar.days.weekDays.size

                displayWeekInMonthIndex == weekInMonthIndex
            }
        }
    }
}
