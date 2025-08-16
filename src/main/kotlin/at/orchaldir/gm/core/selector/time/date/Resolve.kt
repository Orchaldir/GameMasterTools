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

fun resolveYear(date: Year): DisplayYear {
    val year = date.year

    if (year >= 0) {
        return DisplayYear(1, year)
    }

    return DisplayYear(0, -(year + 1))
}

fun resolveDecade(date: Decade): DisplayDecade {
    val decade = date.decade

    if (decade >= 0) {
        return DisplayDecade(1, decade)
    }

    return DisplayDecade(0, -decade - 1)
}

fun resolveCentury(date: Century): DisplayCentury {
    val century = date.century

    if (century >= 0) {
        return DisplayCentury(1, century)
    }

    return DisplayCentury(0, -century - 1)
}

fun resolveMillennium(date: Millennium): DisplayMillennium {
    val millennium = date.millennium

    if (millennium >= 0) {
        return DisplayMillennium(1, millennium)
    }

    return DisplayMillennium(0, -millennium - 1)
}

// resolve display date

fun Calendar.resolve(date: DisplayDate) = when (date) {
    is DisplayDay -> resolveDay(date)
    is DisplayDayRange -> resolveDayRange(date)
    is DisplayWeek -> resolveWeek(date)
    is DisplayMonth -> resolveMonth(date)
    is DisplayYear -> resolveYear(date)
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

fun resolveYear(date: DisplayYear): Year {
    if (date.eraIndex == 1) {
        val year = date.yearIndex

        return Year(year)
    }

    val year = -(date.yearIndex + 1)

    return Year(year)
}

fun resolveDecade(date: DisplayDecade): Decade {
    if (date.eraIndex == 1) {
        val decade = date.decadeIndex

        return Decade(decade)
    }

    val decade = -(date.decadeIndex + 1)

    return Decade(decade)
}

fun resolveCentury(date: DisplayCentury): Century {
    if (date.eraIndex > 0) {
        val century = date.centuryIndex

        return Century(century)
    }

    val century = -(date.centuryIndex + 1)

    return Century(century)
}

fun resolveMillennium(date: DisplayMillennium): Millennium {
    if (date.eraIndex > 0) {
        val century = date.millenniumIndex

        return Millennium(century)
    }

    val millennium = -(date.millenniumIndex + 1)

    return Millennium(millennium)
}