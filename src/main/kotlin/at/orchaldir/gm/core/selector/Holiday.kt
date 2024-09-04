package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.time.Day

fun State.canDelete(holiday: HolidayId) = getCultures(holiday).isEmpty()

fun State.getHolidays(calendar: CalendarId) = getHolidayStorage().getAll()
    .filter { it.calendar == calendar }

fun State.getForHolidays(day: Day) = getHolidayStorage().getAll().filter { holiday ->
    val calendar = getCalendarStorage().getOrThrow(holiday.calendar)
    val displayDay = calendar.resolve(day)

    holiday.relativeDate.isOn(calendar, displayDay)
}
