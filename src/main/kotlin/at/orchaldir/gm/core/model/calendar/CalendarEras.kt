package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CalendarEras

@Serializable
@SerialName("Improved")
data class BeforeAndAfter(
    val before: CalendarEra,
    val after: CalendarEra,
) : CalendarEras() {
    constructor(beforeText: String, beforeIsPrefix: Boolean, afterText: String, afterIsPrefix: Boolean) :
            this(CalendarEra(false, beforeText, beforeIsPrefix), CalendarEra(true, afterText, afterIsPrefix))
}


