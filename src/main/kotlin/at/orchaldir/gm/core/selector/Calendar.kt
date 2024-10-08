package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.ImprovedCalendar
import at.orchaldir.gm.core.model.calendar.OriginalCalendar
import at.orchaldir.gm.core.model.holiday.FixedDayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.WeekdayInMonth
import at.orchaldir.gm.utils.doNothing

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
            is FixedDayInYear -> if (holiday.relativeDate.monthIndex == monthIndex) {
                val requiredDays = holiday.relativeDate.dayIndex + 1

                if (minNumber < requiredDays) {
                    minNumber = requiredDays
                }
            }

            is WeekdayInMonth -> doNothing()
        }
    }

    return minNumber
}

fun getMinNumberOfMonths(holidays: List<Holiday>): Int {
    var minNumber = 2

    holidays.forEach { holiday ->
        val requiredMonths = 1 + when (holiday.relativeDate) {
            is FixedDayInYear -> holiday.relativeDate.monthIndex
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
            is FixedDayInYear -> doNothing()
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
        is FixedDayInYear -> false
        is WeekdayInMonth -> true
    }
}

