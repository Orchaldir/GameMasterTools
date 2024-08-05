package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.*
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
data class BeforeAndCurrent(
    val before: EraBeforeStart,
    val first: FirstEra,
    val eras: List<LaterEra> = emptyList(),
) {
    constructor(
        beforeText: String,
        beforeIsPrefix: Boolean,
        start: Date,
        afterText: String,
        afterIsPrefix: Boolean,
        eras: List<LaterEra> = emptyList(),
    ) :
            this(EraBeforeStart(beforeText, beforeIsPrefix), FirstEra(start, afterText, afterIsPrefix), eras)

    fun getAll() = listOf(before) + eras

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
