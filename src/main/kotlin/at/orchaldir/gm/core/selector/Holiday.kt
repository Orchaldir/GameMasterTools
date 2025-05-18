package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.holiday.HolidayOfCatastrophe
import at.orchaldir.gm.core.model.holiday.HolidayOfGod
import at.orchaldir.gm.core.model.holiday.HolidayOfTreaty
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.time.date.resolveDay

fun State.canDelete(holiday: HolidayId) = getCultures(holiday).isEmpty()
        && getOrganizations(holiday).isEmpty()

fun State.getHolidays(calendar: CalendarId) = getHolidayStorage().getAll()
    .filter { it.calendar == calendar }

fun State.getHolidays(catastrophe: CatastropheId) = getHolidayStorage()
    .getAll()
    .filter { it.purpose is HolidayOfCatastrophe && it.purpose.catastrophe == catastrophe }

fun State.getHolidays(god: GodId) = getHolidayStorage()
    .getAll()
    .filter { it.purpose is HolidayOfGod && it.purpose.god == god }

fun State.getHolidays(treaty: TreatyId) = getHolidayStorage()
    .getAll()
    .filter { it.purpose is HolidayOfTreaty && it.purpose.treaty == treaty }

fun State.getForHolidays(day: Day) = getHolidayStorage()
    .getAll()
    .filter { holiday ->
        val calendar = getCalendarStorage().getOrThrow(holiday.calendar)
        val displayDay = calendar.resolveDay(day)

        holiday.relativeDate.isOn(calendar, displayDay)
    }
