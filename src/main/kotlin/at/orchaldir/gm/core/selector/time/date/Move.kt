package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.*

// up

fun Calendar.moveUp(date: Date): Date? = when (date) {
    is Day -> moveUpDay(date)
    is DayRange -> null
    is Week -> moveUpDayToMonth(getStartDayOfWeek(date))
    is Month -> resolveYear(resolveMonth(date).year)
    is Year -> resolveDecade(resolveYear(date).decade())
    is ApproximateYear -> resolveDecade(resolveYearOrHigher(date, ::DisplayYear).decade())
    is Decade -> resolveCentury(resolveDecade(date).century())
    is Century -> resolveMillennium(resolveCentury(date).millennium())
    is Millennium -> null
}

private fun Calendar.moveUpDay(day: Day): Date {
    val daysPerWeek = days.getDaysPerWeek()

    return if (daysPerWeek > 0) {
        moveUpDayToWeek(day, daysPerWeek)
    } else {
        moveUpDayToMonth(day)
    }
}

fun Calendar.moveUpDayToMonth(day: Day) =
    resolveMonth(resolveDay(day).month)

fun Calendar.moveUpDayToWeek(day: Day): Week {
    val daysPerWeek = getValidDaysPerWeek()

    return moveUpDayToWeek(day, daysPerWeek)
}

private fun moveUpDayToWeek(
    day: Day,
    daysPerWeek: Int,
) = if (day.day >= 0) {
    Week(day.day / daysPerWeek)
} else {
    val week = (day.day - 1) / daysPerWeek
    Week(week)
}

// day

fun Calendar.getStartDay(date: Date) = when (date) {
    is Day -> date
    is DayRange -> date.startDay
    is Week -> getStartDayOfWeek(date)
    is Month -> getStartDayOfMonth(date)
    is Year -> getStartDayOfYear(date)
    is ApproximateYear -> getStartDayOfYear(date.year())
    is Decade -> getStartDayOfDecade(date)
    is Century -> getStartDayOfCentury(date)
    is Millennium -> getStartDayOfMillennium(date)
}

fun Calendar.getStartDisplayDay(date: Date): DisplayDay = when (date) {
    is Day -> resolveDay(date)
    is DayRange -> resolveDay(date.startDay)
    is Week -> resolveDay(getStartDayOfWeek(date))
    is Month -> getStartDisplayDayOfMonth(date)
    is Year -> getStartDisplayDayOfYear(date)
    is ApproximateYear -> getStartDisplayDayOfYear(date.year())
    is Decade -> getStartDisplayDayOfDecade(date)
    is Century -> getStartDisplayDayOfCentury(date)
    is Millennium -> getStartDisplayDayOfMillennium(date)
}

fun Calendar.getEndDay(date: Date) = when (date) {
    is Day -> date
    is DayRange -> date.endDay
    is Week -> getEndDayOfWeek(date)
    is Month -> getEndDayOfMonth(date)
    is Year -> getEndDayOfYear(date)
    is ApproximateYear -> getEndDayOfYear(date.year())
    is Decade -> getEndDayOfDecade(date)
    is Century -> getEndDayOfCentury(date)
    is Millennium -> getEndDayOfMillennium(date)
}

// day range

fun Calendar.getDayRange(date: Date) = DayRange(
    getStartDay(date),
    getEndDay(date),
)

// week

fun Calendar.getStartDayOfWeek(week: Week): Day {
    val daysPerWeek = getValidDaysPerWeek()

    return Day(week.week * daysPerWeek)
}

fun Calendar.getEndDayOfWeek(week: Week): Day {
    val daysPerWeek = getValidDaysPerWeek()

    return Day(week.week * daysPerWeek + daysPerWeek - 1)
}

fun Calendar.getStartWeekOfYear(year: Year) = moveUpDayToWeek(getStartDayOfYear(year))
fun Calendar.getEndWeekOfYear(year: Year) = moveUpDayToWeek(getEndDayOfYear(year))

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

fun Calendar.getStartYear(date: Date): Year = resolveYear(getStartDisplayYear(date))

fun Calendar.getStartDisplayYear(date: Date): DisplayYear = when (date) {
    is Day -> getStartDisplayYearOfDay(date)
    is DayRange -> getStartDisplayYearOfDay(date.startDay)
    is Week -> resolveWeek(date).year
    is Month -> resolveMonth(date).year
    is Year -> resolveYear(date)
    is ApproximateYear -> resolveYearOrHigher(date, ::DisplayYear)
    is Decade -> resolveDecade(date).startYear()
    is Century -> resolveCentury(date).startYear()
    is Millennium -> resolveMillennium(date).startYear()
}

private fun Calendar.getStartDisplayYearOfDay(day: Day) =
    resolveDay(day).month.year

// decade

fun Calendar.getStartDayOfDecade(decade: Decade) = resolveDay(getStartDisplayDayOfDecade(decade))

fun Calendar.getStartDisplayDayOfDecade(decade: Decade) = getStartDisplayDayOfDecade(resolveDecade(decade))

fun Calendar.getStartDisplayDayOfDecade(decade: DisplayDecade) = DisplayDay(decade.startYear(), 0, 0, null)

fun Calendar.getEndDayOfDecade(decade: Decade) = getStartDayOfDecade(decade.nextDecade()).previousDay()

fun Calendar.getStartDecade(date: Date): Decade = resolveDecade(getStartDisplayDecade(date))

fun Calendar.getStartDisplayDecade(date: Date): DisplayDecade = when (date) {
    is Day -> getStartDisplayDecadeOfDay(date)
    is DayRange -> getStartDisplayDecadeOfDay(date.startDay)
    is Week -> resolveWeek(date).year.decade()
    is Month -> resolveMonth(date).year.decade()
    is Year -> resolveYear(date).decade()
    is ApproximateYear -> resolveYearOrHigher(date, ::DisplayYear).decade()
    is Decade -> resolveDecade(date)
    is Century -> resolveCentury(date).startYear().decade()
    is Millennium -> resolveMillennium(date).startYear().decade()
}

private fun Calendar.getStartDisplayDecadeOfDay(day: Day) =
    resolveDay(day).month.year.decade()

// century

fun Calendar.getStartDayOfCentury(century: Century) = resolveDay(getStartDisplayDayOfCentury(century))

fun Calendar.getStartDisplayDayOfCentury(century: Century) = getStartDisplayDayOfCentury(resolveCentury(century))

fun Calendar.getStartDisplayDayOfCentury(century: DisplayCentury) = DisplayDay(century.startYear(), 0, 0, null)

fun Calendar.getEndDayOfCentury(century: Century) = getStartDayOfCentury(century.nextCentury()).previousDay()

fun Calendar.getCentury(date: Date): Century = resolveCentury(getDisplayCentury(date))

fun Calendar.getDisplayCentury(date: Date): DisplayCentury = when (date) {
    is Day -> getDisplayCenturyOfDay(date)
    is DayRange -> getDisplayCenturyOfDay(date.startDay)
    is Week -> resolveWeek(date).year.decade().century()
    is Month -> resolveMonth(date).year.decade().century()
    is Year -> resolveYear(date).decade().century()
    is ApproximateYear -> resolveYearOrHigher(date, ::DisplayYear).decade().century()
    is Decade -> resolveDecade(date).century()
    is Century -> resolveCentury(date)
    is Millennium -> resolveMillennium(date).startYear().decade().century()
}

private fun Calendar.getDisplayCenturyOfDay(day: Day) =
    resolveDay(day).month.year.decade().century()

// millennium

fun Calendar.getStartDayOfMillennium(millennium: Millennium) = resolveDay(getStartDisplayDayOfMillennium(millennium))

fun Calendar.getStartDisplayDayOfMillennium(millennium: Millennium) =
    getStartDisplayDayOfMillennium(resolveMillennium(millennium))

fun Calendar.getStartDisplayDayOfMillennium(millennium: DisplayMillennium) =
    DisplayDay(millennium.startYear(), 0, 0, null)

fun Calendar.getEndDayOfMillennium(millennium: Millennium) =
    getStartDayOfMillennium(millennium.nextMillennium()).previous()

fun Calendar.getMillennium(date: Date): Millennium = resolveMillennium(getDisplayMillennium(date))

fun Calendar.getDisplayMillennium(date: Date): DisplayMillennium = when (date) {
    is Day -> getDisplayMillenniumOfDay(date)
    is DayRange -> getDisplayMillenniumOfDay(date.startDay)
    is Week -> resolveWeek(date).year.decade().century().millennium()
    is Month -> resolveMonth(date).year.decade().century().millennium()
    is Year -> resolveYear(date).decade().century().millennium()
    is ApproximateYear -> resolveYearOrHigher(date, ::DisplayYear).decade().century().millennium()
    is Decade -> resolveDecade(date).century().millennium()
    is Century -> resolveCentury(date).millennium()
    is Millennium -> resolveMillennium(date)
}

private fun Calendar.getDisplayMillenniumOfDay(day: Day) =
    resolveDay(day).month.year.decade().century().millennium()
