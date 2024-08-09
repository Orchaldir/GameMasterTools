package at.orchaldir.gm.core.model.calendar.date

sealed class DisplayDate

data class DisplayDay(
    val eraIndex: Int,
    val yearIndex: Int,
    val monthIndex: Int,
    val dayIndex: Int,
) : DisplayDate()

data class DisplayYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate()
