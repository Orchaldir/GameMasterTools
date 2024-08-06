package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.*
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

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

    fun display(date: DisplayDate) = when (date) {
        is DisplayDay -> display(date)
        is DisplayYear -> display(date)
    }

    fun display(day: DisplayDay) = if (day.yearIndex >= 0) {
        val year = day.yearIndex + 1
        first.display(display(year, day.monthIndex, day.dayIndex))
    } else {
        val year = day.yearIndex.absoluteValue
        before.display(display(year, day.monthIndex, day.dayIndex))
    }

    private fun display(year: Int, month: Int, day: Int) = "$day.$month.$year"

    fun display(year: DisplayYear) = if (year.year >= 0) {
        first.display(year.year + 1)
    } else {
        before.display(year.year.absoluteValue)
    }
}
