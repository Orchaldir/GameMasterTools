package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Improved
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Original
import at.orchaldir.gm.core.model.calendar.date.Date
import at.orchaldir.gm.core.model.calendar.date.DateType
import at.orchaldir.gm.core.model.calendar.date.Day
import at.orchaldir.gm.core.model.calendar.date.Year
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCalendarId(parameters: Parameters, param: String) = CalendarId(parseInt(parameters, param))

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
        parseDate(parameters, START),
        parseEras(parameters),
        origin,
        parseDate(parameters, ORIGIN),
    )
}

fun parseDate(parameters: Parameters, param: String): Date {
    val value = parseInt(parameters, combine(param, DATE))

    return when (parse(parameters, combine(param, TYPE), DateType.Year)) {
        DateType.Day -> Day(value)
        DateType.Year -> Year(value)
    }
}

private fun parseEras(parameters: Parameters) = BeforeAndCurrent(
    parseBeforeStart(parameters),
    parseFirstEra(parameters),
)

private fun parseBeforeStart(parameters: Parameters) =
    EraBeforeStart(
        parseEraName(parameters, BEFORE),
        parseIsPrefix(parameters, BEFORE),
    )

private fun parseFirstEra(parameters: Parameters) =
    FirstEra(
        Year(0),
        parseEraName(parameters, CURRENT),
        parseIsPrefix(parameters, CURRENT),
    )

private fun parseIsPrefix(parameters: Parameters, param: String) =
    parseBool(parameters, combine(param, PREFIX))

private fun parseEraName(parameters: Parameters, param: String) =
    parseName(parameters, combine(param, NAME)) ?: "?"

private fun parseDays(parameters: Parameters) = when (parse(parameters, DAYS, DaysType.DayOfTheMonth)) {
    DaysType.DayOfTheMonth -> DayOfTheMonth
    DaysType.Weekdays -> Weekdays(parseWeekdays(parameters))
}

private fun parseWeekdays(parameters: Parameters): List<WeekDay> {
    val count = parseInt(parameters, WEEK_DAYS, 2)

    return (0..<count)
        .map { parseName(parameters, combine(WEEK_DAY, it)) ?: "${it + 1}.Day" }
        .map { WeekDay(it) }
}

private fun parseMonths(parameters: Parameters): List<Month> {
    val count = parameters.getOrFail(MONTHS).toInt()

    return (0..<count)
        .map { parseMonth(parameters, it) }
}

private fun parseMonth(parameters: Parameters, it: Int) = Month(
    parseName(parameters, combine(MONTH_NAME, it)) ?: "${it + 1}.Month",
    parseInt(parameters, combine(MONTH_DAYS, it), 2)
)

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}