package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEras(
    val before: EraBeforeStart,
    val first: LaterEra,
) {
    constructor(
        beforeText: String,
        beforeIsPrefix: Boolean,
        start: Date,
        afterText: String,
        afterIsPrefix: Boolean,
    ) :
            this(EraBeforeStart(beforeText, beforeIsPrefix), LaterEra(start, afterText, afterIsPrefix))

    fun getAll() = listOf(before, first)

    fun getEra(eraIndex: Int) = if (eraIndex == 1) {
        first
    } else {
        before
    }

    fun display(date: DisplayDate) = when (date) {
        is DisplayDay -> display(date)
        is DisplayYear -> display(date)
        is DisplayDecade -> display(date)
    }

    fun display(day: DisplayDay) = getEra(day.year.eraIndex)
        .display(display(day.year.yearIndex + 1, day.monthIndex + 1, day.dayIndex + 1))

    private fun display(year: Int, month: Int, day: Int) = "$day.$month.$year"

    fun display(year: DisplayYear) = getEra(year.eraIndex)
        .display(year.yearIndex + 1)

    fun display(decade: DisplayDecade) = getEra(decade.eraIndex)
        .display(decade.decadeIndex)
}
