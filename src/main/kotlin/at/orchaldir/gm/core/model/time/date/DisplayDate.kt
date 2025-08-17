package at.orchaldir.gm.core.model.time.date

sealed class DisplayDate {

    abstract fun eraIndex(): Int
    abstract fun index(): Int

}

data class DisplayDay(
    val month: DisplayMonth,
    val dayIndex: Int,
    val weekdayIndex: Int? = null,
) : DisplayDate() {

    constructor(eraIndex: Int, yearIndex: Int, monthIndex: Int, dayIndex: Int, weekdayIndex: Int? = null) :
            this(DisplayMonth(eraIndex, yearIndex, monthIndex), dayIndex, weekdayIndex)

    constructor(year: DisplayYear, monthIndex: Int, dayIndex: Int, weekdayIndex: Int? = null) :
            this(DisplayMonth(year, monthIndex), dayIndex, weekdayIndex)

    override fun eraIndex() = month.eraIndex()
    override fun index() = dayIndex

}

data class DisplayDayRange(
    val start: DisplayDay,
    val end: DisplayDay,
) : DisplayDate() {

    override fun eraIndex() = error("Use start or end directly!")
    override fun index() = error("Use start or end directly!")

}

data class DisplayWeek(
    val year: DisplayYear,
    val weekIndex: Int,
) : DisplayDate() {

    constructor(eraIndex: Int, yearIndex: Int, weekIndex: Int) :
            this(DisplayYear(eraIndex, yearIndex), weekIndex)

    override fun eraIndex() = year.eraIndex()
    override fun index() = weekIndex

}

data class DisplayMonth(
    val year: DisplayYear,
    val monthIndex: Int,
) : DisplayDate() {

    constructor(eraIndex: Int, yearIndex: Int, monthIndex: Int) :
            this(DisplayYear(eraIndex, yearIndex), monthIndex)

    override fun eraIndex() = year.eraIndex
    override fun index() = monthIndex

}

data class DisplayYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex
    override fun index() = yearIndex

    fun decadeIndex() = if (yearIndex <= 8) {
        0
    } else {
        1 + (yearIndex - 9) / 10
    }

    fun decade() = DisplayDecade(eraIndex, decadeIndex())
}

data class DisplayApproximateYear(
    val eraIndex: Int,
    val yearIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex
    override fun index() = yearIndex
}

data class DisplayDecade(
    val eraIndex: Int,
    val decadeIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex
    override fun index() = decadeIndex

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

    fun startYear() = DisplayYear(eraIndex, startYearIndex())
    fun century() = DisplayCentury(eraIndex, decadeIndex / 10)
}

data class DisplayCentury(
    val eraIndex: Int,
    val centuryIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex
    override fun index() = centuryIndex

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

    fun startYear() = DisplayYear(eraIndex, startYearIndex())
    fun millennium() = DisplayMillennium(eraIndex, centuryIndex / 10)
}

data class DisplayMillennium(
    val eraIndex: Int,
    val millenniumIndex: Int,
) : DisplayDate() {

    override fun eraIndex() = eraIndex
    override fun index() = millenniumIndex

    fun startYearIndex() = if (eraIndex == 0) {
        if (millenniumIndex == 0) {
            998
        } else {
            (millenniumIndex + 1) * 1000 - 2
        }
    } else {
        if (millenniumIndex == 0) {
            0
        } else {
            millenniumIndex * 1000 - 1
        }
    }

    fun startYear() = DisplayYear(eraIndex, startYearIndex())
}