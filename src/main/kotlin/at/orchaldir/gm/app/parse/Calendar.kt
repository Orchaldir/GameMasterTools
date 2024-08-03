package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCalendarId(parameters: Parameters, param: String) = CalendarId(parameters[param]?.toInt() ?: 0)

fun parseCalendar(
    parameters: Parameters,
    id: CalendarId,
): Calendar {
    val name = parameters.getOrFail(NAME)
    val origin = parseOrigin(parameters)

    return Calendar(
        id, name,
        parseWeekdays(parameters),
        origin = origin
    )
}

private fun parseWeekdays(parameters: Parameters): List<WeekDay> {
    val count = parameters.getOrFail(WEEK_DAYS).toInt()

    return (0..<count)
        .map { parameters[WEEK_DAY_PREFIX + it] ?: "${it + 1}.Day" }
        .map { WeekDay(it) }
}

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}