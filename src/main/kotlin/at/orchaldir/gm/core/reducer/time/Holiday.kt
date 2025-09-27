package at.orchaldir.gm.core.reducer.time

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.MonthDefinition
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.time.holiday.*
import at.orchaldir.gm.utils.doNothing

fun validateHolidayPurpose(state: State, purpose: HolidayPurpose) {
    when (purpose) {
        Anniversary, Fasting, Festival -> doNothing()
        is HolidayOfCatastrophe -> state.getCatastropheStorage().require(purpose.catastrophe)
        is HolidayOfGod -> state.getGodStorage().require(purpose.god)
        is HolidayOfTreaty -> state.getTreatyStorage().require(purpose.treaty)
        is HolidayOfWar -> state.getWarStorage().require(purpose.war)
    }
}

fun validateRelativeDate(calendar: Calendar, relativeDate: RelativeDate) {
    when (relativeDate) {
        is DayInMonth -> {
            require(relativeDate.dayIndex < calendar.getMinDaysPerMonth()) { "Holiday is outside at least one month!" }
        }

        is DayInYear -> {
            val month = checkMonth(calendar, relativeDate.monthIndex)
            require(relativeDate.dayIndex < month.days) { "Holiday is outside the month ${month.name.text}!" }
        }

        is WeekdayInMonth -> {
            checkMonth(calendar, relativeDate.monthIndex)
            when (calendar.days) {
                DayOfTheMonth -> error("A holiday on a weekday requires a calendar with weekdays!")
                is Weekdays -> require(relativeDate.weekdayIndex < calendar.days.weekDays.size) { "Holiday is on an unknown weekday!" }
            }
        }
    }
}

private fun checkMonth(
    calendar: Calendar,
    monthIndex: Int,
): MonthDefinition {
    require(monthIndex < calendar.months.getSize()) { "Holiday is in an unknown month!" }
    return calendar.months.getMonth(monthIndex)
}
