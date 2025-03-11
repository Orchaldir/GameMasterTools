package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date

fun State.getAgeInYears(date: Date?) = if (date != null) {
    getDefaultCalendar().getDurationInYears(date, time.currentDate)
} else {
    null
}

fun State.getAgeInYears(date: Date) = getDefaultCalendar().getDurationInYears(date, time.currentDate)

fun State.getCurrentYear() = getDefaultCalendar()
    .getYear(time.currentDate)

