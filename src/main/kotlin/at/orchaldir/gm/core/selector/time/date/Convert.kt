package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.DayRange
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

fun State.convertDateToDefault(from: CalendarId, date: Date) =
    convertDateToDefault(getCalendarStorage().getOrThrow(from), date)

fun State.convertDateToDefault(from: Calendar, date: Date) =
    convertDate(from, getDefaultCalendar(), date)

fun convertDate(from: Calendar, to: Calendar, date: Date): Date {
    if (from == to) {
        return date
    }

    val offset = from.getStartDateInDefaultCalendar().day - to.getStartDateInDefaultCalendar().day

    if (date is Day) {
        return date + offset
    }

    val startDay = from.getStartDay(date)
    val endDay = from.getEndDay(date)

    return DayRange(startDay + offset, endDay + offset)
}