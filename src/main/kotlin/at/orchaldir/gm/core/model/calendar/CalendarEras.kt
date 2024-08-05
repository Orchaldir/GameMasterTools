package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.DisplayDate
import at.orchaldir.gm.core.model.calendar.date.DisplayDay
import at.orchaldir.gm.core.model.calendar.date.DisplayYear
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
data class BeforeAndCurrent(
    val before: BeforeStart,
    val first: FirstEra,
) {
    constructor(beforeText: String, beforeIsPrefix: Boolean, afterText: String, afterIsPrefix: Boolean) :
            this(BeforeStart(beforeText, beforeIsPrefix), FirstEra(afterText, afterIsPrefix))

    fun getAll() = listOf(before, first)

    fun resolve(date: DisplayDate) = when (date) {
        is DisplayDay -> resolve(date)
        is DisplayYear -> resolve(date)
    }

    fun resolve(day: DisplayDay) = if (day.yearIndex >= 0) {
        val year = day.yearIndex + 1
        first.resolve(resolve(year, day.monthIndex, day.dayIndex))
    } else {
        val year = day.yearIndex.absoluteValue
        before.resolve(resolve(year, day.monthIndex, day.dayIndex))
    }

    private fun resolve(year: Int, month: Int, day: Int) = "$day.$month.$year"

    fun resolve(year: DisplayYear) = if (year.year >= 0) {
        first.resolve(year.year + 1)
    } else {
        before.resolve(year.year.absoluteValue)
    }
}
