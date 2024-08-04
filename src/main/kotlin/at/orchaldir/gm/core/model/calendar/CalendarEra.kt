package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.CalendarYear
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
data class CalendarEra(
    val countFrom: Boolean,
    val text: String,
    val isPrefix: Boolean,
) {
    fun resolve(year: Int) = if (isPrefix) {
        "$text $year"
    } else {
        "$year $text"
    }
}

@Serializable
data class BeforeAndCurrent(
    val before: CalendarEra,
    val current: CalendarEra,
) {
    constructor(beforeText: String, beforeIsPrefix: Boolean, afterText: String, afterIsPrefix: Boolean) :
            this(CalendarEra(false, beforeText, beforeIsPrefix), CalendarEra(true, afterText, afterIsPrefix))

    fun resolve(year: CalendarYear) = if (year.year >= 0) {
        current.resolve(year.year + 1)
    } else {
        before.resolve(year.year.absoluteValue)
    }
}
