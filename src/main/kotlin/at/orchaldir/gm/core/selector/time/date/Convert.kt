package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar

fun State.convertDateToDefault(from: CalendarId, date: Date) =
    convertDate(getCalendarStorage().getOrThrow(from), getDefaultCalendar(), date)

fun convertDate(from: Calendar, to: Calendar, date: Date): Date {
    if (from == to) {
        return date
    }

    val offset = from.getStartDateInDefaultCalendar().day - to.getStartDateInDefaultCalendar().day
    val startDay = from.getStartDay(date)

    return startDay + offset
}