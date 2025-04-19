package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.*

// up

fun Calendar.moveUp(date: Date): Date? = when (date) {
    is Day -> resolveMonth(resolveDay(date).month)
    is Week -> TODO()
    is Month -> resolveYear(resolveMonth(date).year)
    is Year -> resolveDecade(resolveYear(date).decade())
    is Decade -> resolveCentury(resolveDecade(date).century())
    is Century -> null
}

// day

fun Calendar.getStartDay(date: Date) = when (date) {
    is Day -> date
    is Week -> TODO()
    is Month -> getStartDayOfMonth(date)
    is Year -> getStartDayOfYear(date)
    is Decade -> getStartDayOfDecade(date)
    is Century -> getStartDayOfCentury(date)
}

fun Calendar.getStartDisplayDay(date: Date): DisplayDay = when (date) {
    is Day -> resolveDay(date)
    is Week -> TODO()
    is Month -> getStartDisplayDayOfMonth(date)
    is Year -> getStartDisplayDayOfYear(date)
    is Decade -> getStartDisplayDayOfDecade(date)
    is Century -> getStartDisplayDayOfCentury(date)
}

fun Calendar.getEndDay(date: Date) = when (date) {
    is Day -> date
    is Week -> TODO()
    is Month -> getEndDayOfMonth(date)
    is Year -> getEndDayOfYear(date)
    is Decade -> getEndDayOfDecade(date)
    is Century -> getEndDayOfCentury(date)
}

// month

fun Calendar.getStartDayOfMonth(month: Month) = resolveDay(getStartDisplayDayOfMonth(month))

fun Calendar.getStartDisplayDayOfMonth(month: Month) = getStartDisplayDayOfMonth(resolveMonth(month))

fun Calendar.getStartDisplayDayOfMonth(month: DisplayMonth) = DisplayDay(month, 0, null)

fun Calendar.getEndDayOfMonth(month: Month) = getStartDayOfMonth(month.nextMonth()).previousDay()

fun Calendar.getStartOfMonth(day: Day) = getStartOfMonth(resolveDay(day))

fun Calendar.getStartOfMonth(day: DisplayDay) = resolveDay(day.copy(dayIndex = 0))

fun Calendar.getStartOfNextMonth(day: Day) = getStartOfNextMonth(resolveDay(day))

fun Calendar.getStartOfNextMonth(displayDay: DisplayDay): Day {
    val startOfMonth = getStartOfMonth(displayDay)
    val month = getMonth(displayDay)

    return startOfMonth + month.days
}

fun Calendar.getStartOfPreviousMonth(day: Day): Day {
    val displayDay = resolveDay(day)
    val startOfMonth = getStartOfMonth(displayDay)
    val month = getMonth(startOfMonth - 1)

    return startOfMonth - month.days
}

fun Calendar.getEndOfMonth(day: Day) = getStartOfNextMonth(day) - 1

// year

fun Calendar.getStartDayOfYear(year: Year) = resolveDay(getStartDisplayDayOfYear(year))

fun Calendar.getStartDisplayDayOfYear(year: Year) = getStartDisplayDayOfYear(resolveYear(year))

fun Calendar.getStartDisplayDayOfYear(year: DisplayYear) = DisplayDay(year, 0, 0, null)

fun Calendar.getEndDayOfYear(year: Year) = getStartDayOfYear(year.nextYear()).previousDay()

fun Calendar.getStartYear(date: Date): Year = when (date) {
    is Day -> resolveYear(resolveDay(date).month.year)
    is Week -> TODO()
    is Month -> resolveYear(resolveMonth(date).year)
    is Year -> date
    is Decade -> resolveYear(resolveDecade(date).startYear())
    is Century -> resolveYear(resolveCentury(date).startYear())
}

fun Calendar.getStartDisplayYear(date: Date): DisplayYear = when (date) {
    is Day -> resolveDay(date).month.year
    is Week -> TODO()
    is Month -> resolveMonth(date).year
    is Year -> resolveYear(date)
    is Decade -> resolveDecade(date).startYear()
    is Century -> resolveCentury(date).startYear()
}

// decade

fun Calendar.getStartDayOfDecade(decade: Decade) = resolveDay(getStartDisplayDayOfDecade(decade))

fun Calendar.getStartDisplayDayOfDecade(decade: Decade) = getStartDisplayDayOfDecade(resolveDecade(decade))

fun Calendar.getStartDisplayDayOfDecade(decade: DisplayDecade) = DisplayDay(decade.startYear(), 0, 0, null)

fun Calendar.getEndDayOfDecade(decade: Decade) = getStartDayOfDecade(decade.nextDecade()).previousDay()

fun Calendar.getStartDecade(date: Date): Decade = when (date) {
    is Day -> resolveDecade(resolveDay(date).month.year.decade())
    is Week -> TODO()
    is Month -> resolveDecade(resolveMonth(date).year.decade())
    is Year -> resolveDecade(resolveYear(date).decade())
    is Decade -> date
    is Century -> resolveDecade(resolveCentury(date).startYear().decade())
}

fun Calendar.getStartDisplayDecade(date: Date): DisplayDecade = when (date) {
    is Day -> resolveDay(date).month.year.decade()
    is Week -> TODO()
    is Month -> resolveMonth(date).year.decade()
    is Year -> resolveYear(date).decade()
    is Decade -> resolveDecade(date)
    is Century -> resolveCentury(date).startYear().decade()
}

// century

fun Calendar.getStartDayOfCentury(century: Century) = resolveDay(getStartDisplayDayOfCentury(century))

fun Calendar.getStartDisplayDayOfCentury(century: Century) = getStartDisplayDayOfCentury(resolveCentury(century))

fun Calendar.getStartDisplayDayOfCentury(century: DisplayCentury) = DisplayDay(century.startYear(), 0, 0, null)

fun Calendar.getEndDayOfCentury(century: Century) = getStartDayOfCentury(century.nextCentury()).previousDay()

fun Calendar.getCentury(date: Date): Century = when (date) {
    is Day -> resolveCentury(resolveDay(date).month.year.decade().century())
    is Week -> TODO()
    is Month -> resolveCentury(resolveMonth(date).year.decade().century())
    is Year -> resolveCentury(resolveYear(date).decade().century())
    is Decade -> resolveCentury(resolveDecade(date).century())
    is Century -> date
}

fun Calendar.getDisplayCentury(date: Date): DisplayCentury = when (date) {
    is Day -> resolveDay(date).month.year.decade().century()
    is Week -> TODO()
    is Month -> resolveMonth(date).year.decade().century()
    is Year -> resolveYear(date).decade().century()
    is Decade -> resolveDecade(date).century()
    is Century -> resolveCentury(date)
}
