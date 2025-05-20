package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CalendarEra {
    abstract val text: NotEmptyString
    abstract val isPrefix: Boolean

    fun display(date: String) = if (isPrefix) {
        "${text.text} $date"
    } else {
        "$date ${text.text}"
    }
}

@Serializable
@SerialName("BeforeStart")
data class EraBeforeStart(
    override val text: NotEmptyString,
    override val isPrefix: Boolean,
) : CalendarEra()

@Serializable
@SerialName("Later")
data class LaterEra(
    val startDay: Day,
    override val text: NotEmptyString,
    override val isPrefix: Boolean,
) : CalendarEra()