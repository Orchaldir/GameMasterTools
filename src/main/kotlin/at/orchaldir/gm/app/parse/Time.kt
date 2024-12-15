package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.html.model.parseDay
import at.orchaldir.gm.core.model.calendar.CALENDAR
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.time.Time
import io.ktor.http.*

fun parseTime(
    parameters: Parameters,
    default: Calendar,
) = Time(
    parseCalendarId(parameters, CALENDAR),
    parseDay(parameters, default, CURRENT),
)