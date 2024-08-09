package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CalendarEra {
    abstract val text: String
    abstract val isPrefix: Boolean

    fun display(year: Int) = display(year.toString())

    fun display(date: String) = if (isPrefix) {
        "$text $date"
    } else {
        "$date $text"
    }
}

@Serializable
@SerialName("BeforeStart")
data class EraBeforeStart(
    override val text: String,
    override val isPrefix: Boolean,
) : CalendarEra()

@Serializable
@SerialName("Later")
data class LaterEra(
    val startDate: Date,
    override val text: String,
    override val isPrefix: Boolean,
) : CalendarEra()