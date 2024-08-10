package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.Time
import at.orchaldir.gm.core.model.calendar.*
import io.ktor.http.*

fun parseTime(
    parameters: Parameters,
    default: Calendar,
) = Time(
    parseCalendarId(parameters, CALENDAR),
    parseDay(parameters, default, CURRENT),
)