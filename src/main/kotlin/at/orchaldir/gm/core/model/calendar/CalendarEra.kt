package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.CalendarYear
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEra(
    val countFrom: Boolean,
    val text: String,
    val isPrefix: Boolean,
) {
    fun resolve(year: CalendarYear) = if (isPrefix) {
        "$text ${year.year}"
    } else {
        "${year.year} $text"
    }
}

@Serializable
data class BeforeAndCurrent(
    val before: CalendarEra,
    val current: CalendarEra,
) {
    constructor(beforeText: String, beforeIsPrefix: Boolean, afterText: String, afterIsPrefix: Boolean) :
            this(CalendarEra(false, beforeText, beforeIsPrefix), CalendarEra(true, afterText, afterIsPrefix))
}
