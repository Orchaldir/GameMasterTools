package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDate
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Improved
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Original
import io.ktor.http.*

fun parseCalendarId(parameters: Parameters, param: String) = CalendarId(parseInt(parameters, param))

fun parseCalendar(
    parameters: Parameters,
    default: Calendar,
    id: CalendarId,
): Calendar {
    val name = parseOptionalString(parameters, NAME) ?: "Unknown"
    val origin = parseOrigin(parameters)

    return Calendar(
        id, name,
        parseDays(parameters),
        parseMonths(parameters),
        parseEras(parameters, default),
        origin,
    )
}

private fun parseEras(parameters: Parameters, default: Calendar) = CalendarEras(
    parseBeforeStart(parameters),
    parseFirstEra(parameters, default),
)

private fun parseBeforeStart(parameters: Parameters) =
    EraBeforeStart(
        parseEraName(parameters, BEFORE),
        parseIsPrefix(parameters, BEFORE),
    )

private fun parseFirstEra(parameters: Parameters, default: Calendar) =
    LaterEra(
        parseDate(parameters, default, CURRENT),
        parseEraName(parameters, CURRENT),
        parseIsPrefix(parameters, CURRENT),
    )

private fun parseIsPrefix(parameters: Parameters, param: String) =
    parseBool(parameters, combine(param, PREFIX))

private fun parseEraName(parameters: Parameters, param: String) =
    parseOptionalString(parameters, combine(param, NAME)) ?: "?"

private fun parseDays(parameters: Parameters) = when (parse(parameters, DAYS, DaysType.DayOfTheMonth)) {
    DaysType.DayOfTheMonth -> DayOfTheMonth
    DaysType.Weekdays -> Weekdays(parseWeekdays(parameters))
}

private fun parseWeekdays(parameters: Parameters): List<WeekDay> {
    val count = parseInt(parameters, combine(WEEK, DAYS), 2)

    return (0..<count)
        .map { parseOptionalString(parameters, combine(WEEK, DAY, it)) ?: "${it + 1}.Day" }
        .map { WeekDay(it) }
}

private fun parseMonths(parameters: Parameters): List<Month> {
    val count = parseInt(parameters, MONTHS, 2)

    return (0..<count)
        .map { parseMonth(parameters, it) }
}

private fun parseMonth(parameters: Parameters, it: Int) = Month(
    parseOptionalString(parameters, combine(MONTH, NAME, it)) ?: "${it + 1}.Month",
    parseInt(parameters, combine(MONTH, DAYS, it), 2)
)

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR_TYPE)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}