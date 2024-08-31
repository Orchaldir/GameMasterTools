package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.holiday.HolidayId

fun State.canDelete(holiday: HolidayId) = true

fun State.getHolidays(calendar: CalendarId) = getHolidayStorage().getAll()
    .filter { it.calendar == calendar }
