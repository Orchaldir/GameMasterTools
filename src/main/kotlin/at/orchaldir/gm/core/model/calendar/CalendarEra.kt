package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarEra(
    val countFrom: Boolean,
    val text: String,
    val isPrefix: Boolean,
)

@Serializable
data class BeforeAndCurrent(
    val before: CalendarEra,
    val current: CalendarEra,
) {
    constructor(beforeText: String, beforeIsPrefix: Boolean, afterText: String, afterIsPrefix: Boolean) :
            this(CalendarEra(false, beforeText, beforeIsPrefix), CalendarEra(true, afterText, afterIsPrefix))
}
