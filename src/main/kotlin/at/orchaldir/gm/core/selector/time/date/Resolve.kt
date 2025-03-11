package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.*
import kotlin.math.absoluteValue


fun Calendar.resolve(date: Date) = when (date) {
    is Day -> resolveDay(date)
    is Year -> displayYear(date)
    is Decade -> resolveDecade(date)
    is Century -> resolveCentury(date)
}

fun Calendar.resolveDay(date: Day): DisplayDay {
    val daysPerYear = getDaysPerYear()
    val day = date.day + getOffsetInDays()
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

fun Calendar.displayYear(date: Year): DisplayYear {
    val offsetInYears = getOffsetInYears()
    val year = date.year + offsetInYears

    if (year >= 0) {
        return DisplayYear(1, year)
    }

    return DisplayYear(0, -(year + 1))
}

fun Calendar.resolveDecade(date: Decade): DisplayDecade {
    val offsetInDecades = getOffsetInDecades()
    val decade = date.decade + offsetInDecades

    if (decade >= 0) {
        return DisplayDecade(1, decade)
    }

    return DisplayDecade(0, -decade - 1)
}

fun Calendar.resolveCentury(date: Century): DisplayCentury {
    val offsetInCenturies = getOffsetInCenturies()
    val century = date.century + offsetInCenturies

    if (century >= 0) {
        return DisplayCentury(1, century)
    }

    return DisplayCentury(0, -century - 1)
}

// display

fun Calendar.resolve(date: DisplayDate) = when (date) {
    is DisplayDay -> resolveDay(date)
    is DisplayYear -> resolveYear(date)
    is DisplayDecade -> resolveDecade(date)
    is DisplayCentury -> resolveCentury(date)
}

fun Calendar.resolveDay(day: DisplayDay): Day {
    val daysPerYear = getDaysPerYear()
    val offsetInDays = getOffsetInDays()

    if (day.year.eraIndex == 1) {
        var dayIndex = day.year.yearIndex * daysPerYear + day.dayIndex - offsetInDays

        (0..<day.monthIndex)
            .forEach { dayIndex += months.getDaysPerMonth(it) }

        return Day(dayIndex)
    }

    var dayIndex = -day.year.yearIndex * daysPerYear - offsetInDays

    (day.monthIndex..<months.getSize())
        .forEach { dayIndex -= months.getDaysPerMonth(it) }

    dayIndex += day.dayIndex

    return Day(dayIndex)
}

fun Calendar.resolveYear(date: DisplayYear): Year {
    val offsetInYears = getOffsetInYears()

    if (date.eraIndex == 1) {
        val year = date.yearIndex - offsetInYears

        return Year(year)
    }

    val year = -(date.yearIndex + 1) - offsetInYears

    return Year(year)
}

fun Calendar.resolveDecade(date: DisplayDecade): Decade {
    val offsetInDecades = getOffsetInDecades()

    if (date.eraIndex == 1) {
        val decade = date.decadeIndex - offsetInDecades

        return Decade(decade)
    }

    val decade = -(date.decadeIndex + 1) - offsetInDecades

    return Decade(decade)
}

fun Calendar.resolveCentury(date: DisplayCentury): Century {
    val offsetInDecades = getOffsetInCenturies()

    if (date.eraIndex > 0) {
        val century = date.centuryIndex - offsetInDecades

        return Century(century)
    }

    val decade = -(date.centuryIndex + 1) - offsetInDecades

    return Century(decade)
}