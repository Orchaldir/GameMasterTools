package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.ImprovedCalendar
import at.orchaldir.gm.core.model.calendar.OriginalCalendar
import at.orchaldir.gm.core.model.holiday.FixedDayInYear
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

fun State.getMinNumberOfWeekdays(calendar: CalendarId): Int {
    var minNumber = 2

    getHolidays(calendar).forEach { holiday ->
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
