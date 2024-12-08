package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date

fun State.getAgeInYears(date: Date) = getDefaultCalendar()
    .getDurationInYears(date, time.currentDate)

