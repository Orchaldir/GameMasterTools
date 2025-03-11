package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.DisplayDay

// month

fun Calendar.getStartOfMonth(day: Day) = getStartOfMonth(resolveDay(day))

fun Calendar.getStartOfMonth(day: DisplayDay) = resolveDay(day.copy(dayIndex = 0))

fun Calendar.getStartOfNextMonth(day: Day) = getStartOfNextMonth(resolveDay(day))

fun Calendar.getStartOfNextMonth(displayDay: DisplayDay): Day {
    val startOfMonth = getStartOfMonth(displayDay)
    val month = getMonth(displayDay)

    return startOfMonth + month.days
}

fun Calendar.getStartOfPreviousMonth(day: Day): Day {
    val displayDay = resolveDay(day)
    val startOfMonth = getStartOfMonth(displayDay)
    val month = getMonth(startOfMonth - 1)

    return startOfMonth - month.days
}

fun Calendar.getEndOfMonth(day: Day) = getStartOfNextMonth(day) - 1