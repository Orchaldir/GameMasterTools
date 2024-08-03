package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Improved
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Original
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCalendarId(parameters: Parameters, param: String) = CalendarId(parameters[param]?.toInt() ?: 0)

fun parseCalendar(
    parameters: Parameters,
    id: CalendarId,
): Calendar {
    val name = parseName(parameters, NAME) ?: "Unknown"
    val origin = parseOrigin(parameters)

    return Calendar(
        id, name,
        parseDays(parameters),
        parseMonths(parameters),
        origin,
    )
}

private fun parseDays(parameters: Parameters) = when (parse(parameters, DAYS, DaysType.DayOfTheMonth)) {
    DaysType.DayOfTheMonth -> DayOfTheMonth
    DaysType.Weekdays -> Weekdays(parseWeekdays(parameters))
}

private fun parseWeekdays(parameters: Parameters): List<WeekDay> {
    val count = parameters[WEEK_DAYS]?.toInt() ?: 2

    return (0..<count)
        .map { parseName(parameters, WEEK_DAY_PREFIX + it) ?: "${it + 1}.Day" }
        .map { WeekDay(it) }
}

private fun parseMonths(parameters: Parameters): List<Month> {
    val count = parameters.getOrFail(MONTHS).toInt()

    return (0..<count)
        .map { parseMonth(parameters, it) }
}

private fun parseMonth(parameters: Parameters, it: Int) = Month(
    parseName(parameters, MONTH_NAME_PREFIX + it) ?: "${it + 1}.Month",
    parameters[MONTH_DAYS_PREFIX + it]?.toInt() ?: 2,
)

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}