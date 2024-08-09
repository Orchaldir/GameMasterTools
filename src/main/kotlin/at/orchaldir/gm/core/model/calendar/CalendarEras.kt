package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.Date
import at.orchaldir.gm.core.model.calendar.date.DisplayDate
import at.orchaldir.gm.core.model.calendar.date.DisplayDay
import at.orchaldir.gm.core.model.calendar.date.DisplayYear
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

    fun getEar(eraIndex: Int) = if (eraIndex == 1) {
        first
    } else {
        before
    }

    fun display(date: DisplayDate) = when (date) {
        is DisplayDay -> display(date)
        is DisplayYear -> display(date)
    }

    fun display(day: DisplayDay) = getEar(day.eraIndex)
        .display(display(day.yearIndex + 1, day.monthIndex + 1, day.dayIndex + 1))

    private fun display(year: Int, month: Int, day: Int) = "$day.$month.$year"

    fun display(year: DisplayYear) = getEar(year.eraIndex)
        .display(year.yearIndex + 1)
}
