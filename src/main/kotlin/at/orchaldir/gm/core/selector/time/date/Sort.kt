package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.*

fun Calendar.createSorter(): (Date) -> Int {
    val daysPerYear = getDaysPerYear()
    val daysPerWeek = days.getDaysPerWeek()

    return { date ->
        getDateValue(date, daysPerWeek, daysPerYear)
    }
}

fun Calendar.getDateValue(date: Date): Int {
    val daysPerYear = getDaysPerYear()
    val daysPerWeek = days.getDaysPerWeek()

    return getDateValue(date, daysPerWeek, daysPerYear)
}

private fun Calendar.getDateValue(
    date: Date,
    daysPerWeek: Int,
    daysPerYear: Int,
) = when (date) {
    is Day -> date.day
    is DayRange -> date.startDay.day
    is Week -> date.week * daysPerWeek
    is Month -> getStartDayOfMonth(date).day
    is Year -> date.year * daysPerYear
    is ApproximateYear -> date.year * daysPerYear
    is Decade -> date.decade * daysPerYear * 10
    is Century -> date.century * daysPerYear * 100
    is Millennium -> date.millennium * daysPerYear * 1000
}