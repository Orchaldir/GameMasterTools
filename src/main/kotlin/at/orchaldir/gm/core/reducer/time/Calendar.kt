package at.orchaldir.gm.core.reducer.time

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.utils.doNothing

fun validateDays(
    calendar: Calendar,
) {
    when (val days = calendar.days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> {
            require(days.weekDays.size > 1) { "Requires at least 2 weekdays" }
            require(days.weekDays.map { it.name }.toSet().size == days.weekDays.size) {
                "The names of the weekdays need to be unique!"
            }
        }
    }
}

fun validateMonths(
    calendar: Calendar,
) {
    require(calendar.months.getSize() > 1) { "Requires at least 2 months" }
    calendar.months.months().forEach { require(it.days > 1) { "Requires at least 2 days per month" } }
    require(calendar.months.months().map { it.name }.toSet().size == calendar.months.getSize()) {
        "The names of the months need to be unique!"
    }
}

fun validateEras(
    state: State,
    calendar: Calendar,
) {
    if (state.getDefaultCalendarId() == calendar.id) {
        require(calendar.eras.first.startDay.day == 0) { "Default Calendar must not have an offset!" }
    }
}

fun validateHolidays(
    state: State,
    calendar: Calendar,
) {
    state.getHolidays(calendar.id).forEach { validateRelativeDate(calendar, it.relativeDate) }
}
