package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.selector.time.resolve
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.holiday.HolidayOfGod
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.selector.organization.getOrganizations

fun State.canDelete(holiday: HolidayId) = getCultures(holiday).isEmpty()
        && getOrganizations(holiday).isEmpty()

fun State.getHolidays(calendar: CalendarId) = getHolidayStorage().getAll()
    .filter { it.calendar == calendar }

fun State.getHolidays(god: GodId) = getHolidayStorage().getAll()
    .filter { it.purpose is HolidayOfGod && it.purpose.god == god }

fun State.getForHolidays(day: Day) = getHolidayStorage().getAll().filter { holiday ->
    val calendar = getCalendarStorage().getOrThrow(holiday.calendar)
    val displayDay = calendar.resolve(day)

    holiday.relativeDate.isOn(calendar, displayDay)
}
