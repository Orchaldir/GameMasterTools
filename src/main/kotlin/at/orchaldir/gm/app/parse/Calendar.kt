package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.*
import at.orchaldir.gm.core.model.calendar.ImprovedCalendar
import at.orchaldir.gm.core.model.calendar.OriginalCalendar
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCalendarId(parameters: Parameters, param: String) = CalendarId(parameters[param]?.toInt() ?: 0)

fun parseCalendar(
    parameters: Parameters,
    id: CalendarId,
): Calendar {
    val name = parameters.getOrFail(NAME)
    val origin = parseOrigin(parameters)

    return Calendar(id, name, origin = origin)
}

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}