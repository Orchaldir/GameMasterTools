package at.orchaldir.gm.core.model.time

sealed class DisplayDate

data class DisplayDay(
    val eraIndex: Int,
    val yearIndex: Int,
    val monthIndex: Int,
    val dayIndex: Int,
) : DisplayDate() {

    fun getStartOfMonth() = copy(dayIndex = 0)

}

data class DisplayYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate()
