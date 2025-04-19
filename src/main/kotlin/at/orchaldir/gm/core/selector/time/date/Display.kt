package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DateFormat
import at.orchaldir.gm.core.model.time.calendar.DateOrder
import at.orchaldir.gm.core.model.time.date.*

fun display(calendar: Calendar, date: Date) =
    display(calendar, calendar.defaultFormat, date)

fun display(calendar: Calendar, displayDate: DisplayDate) =
    display(calendar, calendar.defaultFormat, displayDate)

fun display(calendar: Calendar, format: DateFormat, date: Date) =
    display(calendar, format, calendar.resolve(date))

fun display(calendar: Calendar, format: DateFormat, displayDate: DisplayDate): String {
    val textWithoutEra = displayWithoutEra(calendar, format, displayDate)

    return calendar.eras.getEra(displayDate.eraIndex())
        .display(textWithoutEra)
}

fun displayWithoutEra(calendar: Calendar, format: DateFormat, displayDate: DisplayDate) = when (displayDate) {
    is DisplayDay -> displayDay(calendar, format, displayDate)
    is DisplayWeek -> displayWeek(displayDate)
    is DisplayMonth -> displayMonth(calendar, format, displayDate)
    is DisplayYear -> (displayDate.yearIndex + 1).toString()
    is DisplayDecade -> (displayDate.decadeIndex * 10).toString() + "s"
    is DisplayCentury -> {
        val century = displayDate.centuryIndex + 1
        "$century.century"
    }
}

private fun displayDay(calendar: Calendar, format: DateFormat, displayDay: DisplayDay): String {
    val month = getMonthString(format, calendar, displayDay.month)

    return displayDay(format, displayDay.dayIndex + 1, month, displayDay.month.year.yearIndex + 1)
}

private fun displayDay(format: DateFormat, day: Int, month: String, year: Int) = when (format.order) {
    DateOrder.DayMonthYear -> displayDay(day, month, year, format.separator)
    DateOrder.YearMonthDay -> displayDay(year, month, day, format.separator)
}

private fun displayDay(part0: Int, part1: String, part2: Int, separator: Char) =
    "$part0$separator$part1$separator$part2"

private fun displayWeek(week: DisplayWeek) =
    displayWeek(week.weekIndex + 1, "Week", week.year.yearIndex + 1)

private fun displayWeek(week: Int, word: String, year: Int) =
    "$week.$word of $year"

private fun displayMonth(calendar: Calendar, format: DateFormat, displayMonth: DisplayMonth): String {
    val month = getMonthString(format, calendar, displayMonth)

    return displayMonth(format, month, displayMonth.year.yearIndex + 1)
}

private fun displayMonth(format: DateFormat, month: String, year: Int) = when (format.order) {
    DateOrder.DayMonthYear -> displayMonth(month, year.toString(), format.separator)
    DateOrder.YearMonthDay -> displayMonth(year.toString(), month, format.separator)
}

private fun displayMonth(part0: String, part1: String, separator: Char) =
    "$part0$separator$part1"

private fun getMonthString(
    format: DateFormat,
    calendar: Calendar,
    displayMonth: DisplayMonth,
) = if (format.displayMonthNames) {
    calendar.getMonth(displayMonth).name
} else {
    (displayMonth.monthIndex + 1).toString()
}