package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.*
import kotlin.math.absoluteValue

// resolve date

fun Calendar.resolve(date: Date) = when (date) {
    is Day -> resolveDay(date)
    is DayRange -> resolveDayRange(date)
    is Week -> resolveWeek(date)
    is Month -> resolveMonth(date)
    is Year -> resolveYear(date)
    is ApproximateYear -> resolveApproximateYear(date)
    is Decade -> resolveDecade(date)
    is Century -> resolveCentury(date)
    is Millennium -> resolveMillennium(date)
}

fun Calendar.resolveDay(date: Day): DisplayDay {
    val daysPerYear = getDaysPerYear()
    val day = date.day
    val weekdayIndex = getWeekDay(date)

    if (day >= 0) {
        val year = day / daysPerYear
        val remainingDays = day % daysPerYear
        val (monthIndex, dayIndex) = resolveDayAndMonth(remainingDays)

        return DisplayDay(1, year, monthIndex, dayIndex, weekdayIndex)
    }

    val absoluteDate = day.absoluteValue - 1
    val year = absoluteDate / daysPerYear
    var remainingDays = absoluteDate % daysPerYear

    for ((monthIndex, monthData) in months.months().withIndex().reversed()) {
        if (remainingDays < monthData.days) {
            val dayIndex = monthData.days - remainingDays - 1
            return DisplayDay(0, year, monthIndex, dayIndex, weekdayIndex)
        }

        remainingDays -= monthData.days
    }

    error("Unreachable")
}

fun Calendar.resolveDayRange(date: DayRange) = DisplayDayRange(resolveDay(date.startDay), resolveDay(date.endDay))

fun Calendar.resolveDayAndMonth(dayInYear: Int): Pair<Int, Int> {
    var remainingDays = dayInYear

    for ((monthIndex, monthData) in months.months().withIndex()) {
        if (remainingDays < monthData.days) {
            return Pair(monthIndex, remainingDays)
        }

        remainingDays -= monthData.days
    }

    error("Unreachable")
}

fun Calendar.resolveWeek(date: Week): DisplayWeek {
    val daysPerWeek = getValidDaysPerWeek()
    val daysPerYear = getDaysPerYear()
    val day = date.week * daysPerWeek

    val year = if (day >= 0) {
        DisplayYear(1, day / daysPerYear)
    } else {
        val absoluteDate = day.absoluteValue - 1
        DisplayYear(0, absoluteDate / daysPerYear)
    }
    val startDay = resolveDay(getStartDisplayDayOfYear(year))
    val startWeek = startDay.day / daysPerWeek

    return DisplayWeek(year, date.week - startWeek)
}

fun Calendar.resolveMonth(date: Month): DisplayMonth {
    val monthsPerYear = getMonthsPerYear()
    val month = date.month

    if (month >= 0) {
        val year = month / monthsPerYear
        val remainingMonths = month % monthsPerYear

        return DisplayMonth(1, year, remainingMonths)
    }

    val absoluteDate = month.absoluteValue - 1
    val year = absoluteDate / monthsPerYear
    val remainingMonths = absoluteDate % monthsPerYear

    return DisplayMonth(0, year, monthsPerYear - remainingMonths - 1)
}

fun resolveYear(date: Year) = resolveYearOrHigher(date, ::DisplayYear)
fun resolveApproximateYear(date: ApproximateYear) = resolveYearOrHigher(date, ::DisplayApproximateYear)
fun resolveDecade(date: Decade) = resolveYearOrHigher(date, ::DisplayDecade)
fun resolveCentury(date: Century) = resolveYearOrHigher(date, ::DisplayCentury)
fun resolveMillennium(date: Millennium) = resolveYearOrHigher(date, ::DisplayMillennium)

fun <I : Date, O : DisplayDate> resolveYearOrHigher(date: I, create: (Int, Int) -> O): O {
    val index = date.getIndex()

    if (index >= 0) {
        return create(1, index)
    }

    return create(0, -(index + 1))
}

// resolve display date

fun Calendar.resolve(date: DisplayDate) = when (date) {
    is DisplayDay -> resolveDay(date)
    is DisplayDayRange -> resolveDayRange(date)
    is DisplayWeek -> resolveWeek(date)
    is DisplayMonth -> resolveMonth(date)
    is DisplayYear -> resolveYear(date)
    is DisplayApproximateYear -> resolveApproximateYear(date)
    is DisplayDecade -> resolveDecade(date)
    is DisplayCentury -> resolveCentury(date)
    is DisplayMillennium -> resolveMillennium(date)
}

fun Calendar.resolveDay(day: DisplayDay): Day {
    val daysPerYear = getDaysPerYear()
    val month = day.month

    if (month.year.eraIndex == 1) {
        var dayIndex = month.year.yearIndex * daysPerYear + day.dayIndex

        (0..<month.monthIndex)
            .forEach { dayIndex += months.getDaysPerMonth(it) }

        return Day(dayIndex)
    }

    var dayIndex = -month.year.yearIndex * daysPerYear

    (month.monthIndex..<months.getSize())
        .forEach { dayIndex -= months.getDaysPerMonth(it) }

    dayIndex += day.dayIndex

    return Day(dayIndex)
}

fun Calendar.resolveDayRange(range: DisplayDayRange) = DayRange(resolveDay(range.start), resolveDay(range.end))

fun Calendar.resolveWeek(week: DisplayWeek): Week {
    val daysPerWeek = getValidDaysPerWeek()
    val startDay = resolveDay(getStartDisplayDayOfYear(week.year))
    val startWeek = startDay.day / daysPerWeek

    return Week(startWeek + week.weekIndex)
}

fun Calendar.resolveMonth(month: DisplayMonth): Month {
    val monthsPerYear = getMonthsPerYear()

    if (month.year.eraIndex == 1) {
        val monthIndex = month.year.yearIndex * monthsPerYear + month.monthIndex

        return Month(monthIndex)
    }

    val monthIndex = -(month.year.yearIndex + 1) * monthsPerYear + month.monthIndex

    return Month(monthIndex)
}

fun resolveYear(date: DisplayYear) = resolveYear(date, ::Year)
fun resolveApproximateYear(date: DisplayApproximateYear) = resolveYear(date, ::ApproximateYear)
fun resolveDecade(date: DisplayDecade) = resolveYear(date, ::Decade)
fun resolveCentury(date: DisplayCentury) = resolveYear(date, ::Century)
fun resolveMillennium(date: DisplayMillennium) = resolveYear(date, ::Millennium)

fun <I : DisplayDate, O : Date> resolveYear(date: I, create: (Int) -> O) = if (date.eraIndex() == 1) {
    create(date.index())
} else {
    create(-(date.index() + 1))
}
