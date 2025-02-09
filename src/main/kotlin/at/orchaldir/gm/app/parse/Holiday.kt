package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDayIndex
import at.orchaldir.gm.core.model.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.holiday.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseHolidayId(
    parameters: Parameters,
    param: String,
) = HolidayId(parseInt(parameters, param))

fun parseHolidayId(value: String) = HolidayId(value.toInt())

fun parseHoliday(id: HolidayId, parameters: Parameters): Holiday {
    val name = parameters.getOrFail(NAME)

    return Holiday(
        id,
        name,
        parseCalendarId(parameters, CALENDAR_TYPE),
        parseRelativeDate(parameters, DATE),
    )
}

fun parseRelativeDate(parameters: Parameters, param: String): RelativeDate {
    return when (parse(parameters, combine(param, TYPE), RelativeDateType.DayInYear)) {
        RelativeDateType.DayInMonth -> DayInMonth(
            parseDayIndex(parameters, param),
        )
        RelativeDateType.DayInYear -> DayInYear(
            parseDayIndex(parameters, param),
            parseInt(parameters, combine(param, MONTH)),
        )

        RelativeDateType.WeekdayInMonth -> WeekdayInMonth(
            parseInt(parameters, combine(param, DAY)),
            parseInt(parameters, combine(param, WEEK)),
            parseInt(parameters, combine(param, MONTH)),
        )
    }
}