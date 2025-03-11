package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.DisplayDate
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DateFormat
import at.orchaldir.gm.core.model.time.calendar.resolve

fun display(calendar: Calendar, date: Date) = display(calendar, calendar.defaultFormat, date)

fun display(calendar: Calendar, format: DateFormat, date: Date): String {
    val displayDate = calendar.resolve(date)
    val textWithoutEra = displayWithoutEra(calendar, format, displayDate)

    return calendar.eras.getEra(displayDate.eraIndex())
        .display(textWithoutEra)
}

fun displayWithoutEra(calendar: Calendar, format: DateFormat, displayDate: DisplayDate): String {
    return ""
}
