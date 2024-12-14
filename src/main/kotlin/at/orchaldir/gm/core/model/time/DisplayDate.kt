package at.orchaldir.gm.core.model.time

sealed class DisplayDate

data class DisplayDay(
    val year: DisplayYear,
    val monthIndex: Int,
    val dayIndex: Int,
    val weekdayIndex: Int? = null,
) : DisplayDate() {

    constructor(eraIndex: Int, yearIndex: Int, monthIndex: Int, dayIndex: Int, weekdayIndex: Int? = null) :
            this(DisplayYear(eraIndex, yearIndex), monthIndex, dayIndex, weekdayIndex)

}

data class DisplayYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate() {

    fun decadeIndex() = yearIndex / 10
}

data class DisplayDecade(
    val eraIndex: Int,
    val decadeIndex: Int,
) : DisplayDate() {

    fun startYearIndex() = if (eraIndex == 0) {
        (decadeIndex + 1) * 10 - 1
    } else {
        decadeIndex * 10
    }

    fun display() = (decadeIndex * 10).toString() + "s"

    fun year() = DisplayYear(eraIndex, startYearIndex())
}