package at.orchaldir.gm.core.model.calendar.date

sealed class CalendarDate

data class CalendarDay(
    val yearIndex: Int,
    val monthIndex: Int,
    val dayIndex: Int,
) : CalendarDate()

data class CalendarYear(val year: Int) : CalendarDate()
