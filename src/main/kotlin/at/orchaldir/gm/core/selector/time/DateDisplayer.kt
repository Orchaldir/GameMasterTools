package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.DisplayDate
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.resolve

fun display(calendar: Calendar, date: Date): String {
    val displayDate = calendar.resolve(date)
    val textWithoutEra = displayWithoutEra(calendar, displayDate)

    return calendar.eras.getEra(displayDate.eraIndex())
        .display(textWithoutEra)
}

fun displayWithoutEra(calendar: Calendar, displayDate: DisplayDate): String {
    return ""
}
