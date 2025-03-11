package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DateFormat
import at.orchaldir.gm.core.model.time.calendar.DateOrder
import at.orchaldir.gm.core.model.time.calendar.resolve

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
    is DisplayYear -> (displayDate.yearIndex + 1).toString()
    is DisplayDecade -> (displayDate.decadeIndex * 10).toString() + "s"
    is DisplayCentury -> {
        val century = displayDate.centuryIndex + 1
        "$century.century"
    }
}

private fun displayDay(calendar: Calendar, format: DateFormat, displayDay: DisplayDay): String {
    val month = if (format.displayMonthNames) {
        calendar.getMonth(displayDay).name
    } else {
        (displayDay.monthIndex + 1).toString()
    }

    return displayDay(format, displayDay.dayIndex + 1, month, displayDay.year.yearIndex + 1)
}

private fun displayDay(format: DateFormat, day: Int, month: String, year: Int) = when (format.order) {
    DateOrder.DayMonthYear -> displayDay(day, month, year, format.separator)
    DateOrder.YearMonthDay -> displayDay(year, month, day, format.separator)
}

private fun displayDay(part0: Int, part1: String, part2: Int, separator: Char) =
    "$part0$separator$part1$separator$part2"
