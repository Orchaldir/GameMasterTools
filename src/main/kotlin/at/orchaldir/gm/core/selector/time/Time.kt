package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getStartYear

fun State.getAgeInYears(date: Date?) = if (date != null) {
    getDefaultCalendar().getDurationInYears(date, time.currentDate)
} else {
    null
}

fun State.getAgeInYears(date: Date) = getDefaultCalendar().getDurationInYears(date, time.currentDate)

fun State.getCurrentYear() = getDefaultCalendar()
    .getStartYear(time.currentDate)

