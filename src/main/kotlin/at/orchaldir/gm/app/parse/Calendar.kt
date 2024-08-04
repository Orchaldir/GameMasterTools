package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Improved
import at.orchaldir.gm.core.model.calendar.CalendarOriginType.Original
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
        parseInt(parameters, OFFSET),
        parseEras(parameters),
        origin,
    )
}

private fun parseEras(parameters: Parameters) = BeforeAndNow(
    parseEra(parameters, BEFORE_PREFIX, false),
    parseEra(parameters, NOW_PREFIX, true),
)

private fun parseEra(parameters: Parameters, param: String, countFrom: Boolean) =
    CalendarEra(
        countFrom,
        parseName(parameters, param + NAME) ?: "?",
        parseBool(parameters, param + PREFIX),
    )


private fun parseDays(parameters: Parameters) = when (parse(parameters, DAYS, DaysType.DayOfTheMonth)) {
    DaysType.DayOfTheMonth -> DayOfTheMonth
    DaysType.Weekdays -> Weekdays(parseWeekdays(parameters))
}

private fun parseWeekdays(parameters: Parameters): List<WeekDay> {
    val count = parseInt(parameters, WEEK_DAYS, 2)

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
    parseInt(parameters, MONTH_DAYS_PREFIX + it, 2)
)

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}