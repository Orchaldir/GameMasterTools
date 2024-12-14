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

    fun decadeIndex() = if (eraIndex == 1 && yearIndex <= 8) {
        0
    } else {
        1 + (yearIndex - 9) / 10
    }

    fun decade() = DisplayDecade(eraIndex, decadeIndex())
}

data class DisplayDecade(
    val eraIndex: Int,
    val decadeIndex: Int,
) : DisplayDate() {

    fun startYearIndex() = if (eraIndex == 0) {
        if (decadeIndex == 0) {
            8
        } else {
            (decadeIndex + 1) * 10 - 2
        }
    } else {
        if (decadeIndex == 0) {
            0
        } else {
            decadeIndex * 10 - 1
        }
    }

    fun display() = (decadeIndex * 10).toString() + "s"

    fun year() = DisplayYear(eraIndex, startYearIndex())
}