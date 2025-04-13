package at.orchaldir.gm.core.selector.time.calendar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.DayInMonth
import at.orchaldir.gm.core.model.holiday.DayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.WeekdayInMonth
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.ImprovedCalendar
import at.orchaldir.gm.core.model.time.calendar.OriginalCalendar
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.getHolidays
import at.orchaldir.gm.utils.doNothing
import kotlin.math.max

fun State.canDelete(calendar: CalendarId) = getChildren(calendar).isEmpty() &&
        getCultures(calendar).isEmpty() &&
        getHolidays(calendar).isEmpty()

fun State.getChildren(calendar: CalendarId) = getCalendarStorage().getAll().filter {
    when (it.origin) {
        is ImprovedCalendar -> it.origin.parent == calendar
        OriginalCalendar -> false
    }
}

fun State.getDefaultCalendar() = getCalendarStorage().getOrThrow(time.defaultCalendar)

fun State.getPossibleParents(calendar: CalendarId) = getCalendarStorage().getAll().filter { it.id != calendar }

fun getMinNumberOfDays(holidays: List<Holiday>, monthIndex: Int): Int {
    var minNumber = 2

    holidays.forEach { holiday ->
        when (holiday.relativeDate) {
            is DayInMonth -> minNumber = updateMinNumber(minNumber, holiday.relativeDate.dayIndex)

            is DayInYear -> if (holiday.relativeDate.monthIndex == monthIndex) {
                minNumber = updateMinNumber(minNumber, holiday.relativeDate.dayIndex)
            }

            is WeekdayInMonth -> doNothing()
        }
    }

    return minNumber
}

fun getMinNumberOfDays(holidays: List<Holiday>): Int {
    var minNumber = 2

    holidays.forEach { holiday ->
        when (holiday.relativeDate) {
            is DayInMonth -> minNumber = updateMinNumber(minNumber, holiday.relativeDate.dayIndex)
            is DayInYear -> minNumber = updateMinNumber(minNumber, holiday.relativeDate.dayIndex)
            is WeekdayInMonth -> doNothing()
        }
    }

    return minNumber
}

private fun updateMinNumber(minNumber: Int, dayIndex: Int) = max(minNumber, dayIndex + 1)

fun getMinNumberOfMonths(holidays: List<Holiday>): Int {
    var minNumber = 2

    holidays.forEach { holiday ->
        val requiredMonths = 1 + when (holiday.relativeDate) {
            is DayInMonth -> 0
            is DayInYear -> holiday.relativeDate.monthIndex
            is WeekdayInMonth -> holiday.relativeDate.monthIndex
        }

        if (minNumber < requiredMonths) {
            minNumber = requiredMonths
        }
    }

    return minNumber
}

fun getMinNumberOfWeekdays(holidays: List<Holiday>): Int {
    var minNumber = 2

    holidays.forEach { holiday ->
        when (holiday.relativeDate) {
            is DayInMonth, is DayInYear -> doNothing()
            is WeekdayInMonth -> {
                val requiredWeekdays = holiday.relativeDate.weekdayIndex + 1

                if (minNumber < requiredWeekdays) {
                    minNumber = requiredWeekdays
                }
            }
        }
    }

    return minNumber
}

fun supportsDayOfTheMonth(holidays: List<Holiday>) = holidays.none { holiday ->
    when (holiday.relativeDate) {
        is DayInMonth, is DayInYear -> false
        is WeekdayInMonth -> true
    }
}

