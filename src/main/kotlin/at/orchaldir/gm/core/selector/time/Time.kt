package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.date.getStartYear

fun State.getAgeInYears(date: Date?) = if (date != null) {
    getDefaultCalendar().getDurationInYears(date, getCurrentDate())
} else {
    null
}

fun State.getAgeInYears(date: Date) = getDefaultCalendar().getDurationInYears(date, getCurrentDate())

fun State.getCurrentYear() = getDefaultCalendar()
    .getStartYear(getCurrentDate())

fun State.getDefaultCalendarId() = data.time.defaultCalendar

fun State.getCurrentDate() = data.time.currentDate

