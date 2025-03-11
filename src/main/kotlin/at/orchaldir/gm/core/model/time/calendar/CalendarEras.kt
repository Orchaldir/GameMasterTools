package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.date.Date
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
}
