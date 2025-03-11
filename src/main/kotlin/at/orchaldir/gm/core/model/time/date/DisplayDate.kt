package at.orchaldir.gm.core.model.time.date

sealed class DisplayDate {

    abstract fun eraIndex(): Int

}

data class DisplayDay(
    val year: DisplayYear,
    val monthIndex: Int,
    val dayIndex: Int,
    val weekdayIndex: Int? = null,
) : DisplayDate() {

    constructor(eraIndex: Int, yearIndex: Int, monthIndex: Int, dayIndex: Int, weekdayIndex: Int? = null) :
            this(DisplayYear(eraIndex, yearIndex), monthIndex, dayIndex, weekdayIndex)

    override fun eraIndex() = year.eraIndex

}

data class DisplayYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex

    fun decadeIndex() = if (yearIndex <= 8) {
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

    override fun eraIndex() = eraIndex

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

    fun year() = DisplayYear(eraIndex, startYearIndex())
    fun century() = DisplayCentury(eraIndex, decadeIndex / 10)
}

data class DisplayCentury(
    val eraIndex: Int,
    val centuryIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex

    fun startYearIndex() = if (eraIndex == 0) {
        if (centuryIndex == 0) {
            98
        } else {
            (centuryIndex + 1) * 100 - 2
        }
    } else {
        if (centuryIndex == 0) {
            0
        } else {
            centuryIndex * 100 - 1
        }
    }

    fun year() = DisplayYear(eraIndex, startYearIndex())
}