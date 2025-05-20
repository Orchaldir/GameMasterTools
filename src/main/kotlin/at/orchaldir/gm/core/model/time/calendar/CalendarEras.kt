package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEras(
    val before: EraBeforeStart,
    val first: LaterEra,
) {
    constructor(
        beforeText: String,
        beforeIsPrefix: Boolean,
        start: Day,
        afterText: String,
        afterIsPrefix: Boolean,
    ) : this(
        EraBeforeStart(NotEmptyString.init(beforeText), beforeIsPrefix),
        LaterEra(start, NotEmptyString.init(afterText), afterIsPrefix),
    )

    constructor(start: Day = Day(0)) : this("BC", true, start, "AD", false)

    fun getAll() = listOf(before, first)

    fun getEra(eraIndex: Int) = if (eraIndex == 1) {
        first
    } else {
        before
    }
}
