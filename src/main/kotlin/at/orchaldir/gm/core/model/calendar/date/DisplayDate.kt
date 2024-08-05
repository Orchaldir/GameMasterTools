package at.orchaldir.gm.core.model.calendar.date

sealed class DisplayDate

data class DisplayDay(
    val yearIndex: Int,
    val monthIndex: Int,
    val dayIndex: Int,
) : DisplayDate()

data class DisplayYear(val year: Int) : DisplayDate()
