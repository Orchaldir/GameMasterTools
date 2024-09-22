package at.orchaldir.gm.core.model.time

sealed class DisplayDate

data class DisplayDay(
    val year: DisplayYear,
    val monthIndex: Int,
    val dayIndex: Int,
    val weekdayIndex: Int? = null,
) : DisplayDate()

data class DisplayYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate()
