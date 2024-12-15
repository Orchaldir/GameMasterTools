package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
import kotlin.math.absoluteValue


fun Calendar.resolve(date: Date) = when (date) {
    is Day -> resolve(date)
    is Year -> resolve(date)
    is Decade -> resolve(date)
}

fun Calendar.resolve(date: Day): DisplayDay {
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

    for ((monthIndex, monthData) in months.withIndex().reversed()) {
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

    for ((monthIndex, monthData) in months.withIndex()) {
        if (remainingDays < monthData.days) {
            return Pair(monthIndex, remainingDays)
        }

        remainingDays -= monthData.days
    }

    error("Unreachable")
}

fun Calendar.resolve(date: Year): DisplayYear {
    val offsetInYears = getOffsetInYears()
    val year = date.year + offsetInYears

    if (year >= 0) {
        return DisplayYear(1, year)
    }

    return DisplayYear(0, -(year + 1))
}

fun Calendar.resolve(date: Decade): DisplayDecade {
    val offsetInDecades = getOffsetInDecades()
    val decade = date.decade + offsetInDecades

    if (decade >= 0) {
        return DisplayDecade(1, decade)
    }

    return DisplayDecade(0, -decade - 1)
}

fun Calendar.resolve(date: DisplayDate) = when (date) {
    is DisplayDay -> resolve(date)
    is DisplayYear -> resolve(date)
    is DisplayDecade -> resolve(date)
}

fun Calendar.resolve(day: DisplayDay): Day {
    val daysPerYear = getDaysPerYear()
    val offsetInDays = getOffsetInDays()

    if (day.year.eraIndex == 1) {
        var dayIndex = day.year.yearIndex * daysPerYear + day.dayIndex - offsetInDays

        (0..<day.monthIndex).map { months[it] }
            .forEach { dayIndex += it.days }

        return Day(dayIndex)
    }

    var dayIndex = -day.year.yearIndex * daysPerYear - offsetInDays

    (day.monthIndex..<months.size).map { months[it] }
        .forEach { dayIndex -= it.days }

    dayIndex += day.dayIndex

    return Day(dayIndex)
}

fun Calendar.resolve(date: DisplayYear): Year {
    val offsetInYears = getOffsetInYears()

    if (date.eraIndex == 1) {
        val year = date.yearIndex - offsetInYears

        return Year(year)
    }

    val year = -(date.yearIndex + 1) - offsetInYears

    return Year(year)
}

fun Calendar.resolve(date: DisplayDecade): Decade {
    val offsetInDecades = getOffsetInDecades()

    if (date.eraIndex == 1) {
        val decade = date.decadeIndex - offsetInDecades

        return Decade(decade)
    }

    val decade = -(date.decadeIndex + 1) - offsetInDecades

    return Decade(decade)
}