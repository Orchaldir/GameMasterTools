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
    val textWithoutEra = when (displayDate) {
        is DisplayDay -> displayDay(calendar, format, displayDate)
        is DisplayDayRange -> return displayDayRange(calendar, format, displayDate)
        is DisplayWeek -> return displayWeek(calendar, displayDate)
        is DisplayMonth -> displayMonth(calendar, format, displayDate)
        is DisplayYear -> (displayDate.yearIndex + 1).toString()
        is DisplayDecade -> (displayDate.decadeIndex * 10).toString() + "s"
        is DisplayCentury -> {
            val century = displayDate.centuryIndex + 1
            "$century.century"
        }
        is DisplayMillennium -> {
            val century = displayDate.millenniumIndex + 1
            "$century.millennium"
        }
    }

    return calendar.eras.getEra(displayDate.eraIndex())
        .display(textWithoutEra)
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

private fun displayDayRange(calendar: Calendar, format: DateFormat, range: DisplayDayRange) =
    display(calendar, format, range.start) + " to " + display(calendar, format, range.end)

private fun displayWeek(calendar: Calendar, week: DisplayWeek): String {
    val year = week.year.yearIndex + 1
    val yearText = calendar.eras.getEra(week.eraIndex())
        .display(year.toString())
    return displayWeek(week.weekIndex + 1, "Week", yearText)
}

private fun displayWeek(week: Int, word: String, year: String) =
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
    calendar.getMonth(displayMonth).name.text
} else {
    (displayMonth.monthIndex + 1).toString()
}