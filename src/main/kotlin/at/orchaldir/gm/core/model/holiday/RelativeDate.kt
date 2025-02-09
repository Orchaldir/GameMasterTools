package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.calendar.Weekdays
import at.orchaldir.gm.core.model.time.DisplayDay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RelativeDateType {
    DayInYear,
    WeekdayInMonth,
}

@Serializable
sealed class RelativeDate {

    fun getType() = when (this) {
        is DayInYear -> RelativeDateType.DayInYear
        is WeekdayInMonth -> RelativeDateType.WeekdayInMonth
    }

    abstract fun display(calendar: Calendar): String
    abstract fun isOn(calendar: Calendar, displayDay: DisplayDay): Boolean
}

@Serializable
@SerialName("DayInYear")
data class DayInYear(val dayIndex: Int, val monthIndex: Int) : RelativeDate() {
    override fun display(calendar: Calendar): String {
        val month = calendar.months[monthIndex]
        val day = dayIndex + 1

        return "${day}.${month.name}"
    }

    override fun isOn(calendar: Calendar, displayDay: DisplayDay) =
        monthIndex == displayDay.monthIndex && dayIndex == displayDay.dayIndex
}

@Serializable
@SerialName("WeekdayInMonth")
data class WeekdayInMonth(val weekdayIndex: Int, val weekInMonthIndex: Int, val monthIndex: Int) : RelativeDate() {
    override fun display(calendar: Calendar): String {
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

    override fun isOn(calendar: Calendar, displayDay: DisplayDay): Boolean {
        if (monthIndex != displayDay.monthIndex) {
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
