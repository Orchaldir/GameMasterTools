package at.orchaldir.gm.core.model.calendar.date

sealed class CalendarDate

data class CalendarDay(
    val year: Int,
    val month: Int,
    val day: Int,
) : CalendarDate()

data class CalendarYear(val year: Int) : CalendarDate()
