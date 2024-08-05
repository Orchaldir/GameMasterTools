package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.Date
import at.orchaldir.gm.core.model.calendar.date.Year
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CalendarEra {
    abstract val text: String
    abstract val isPrefix: Boolean

    fun resolve(year: Int) = resolve(year.toString())

    fun resolve(date: String) = if (isPrefix) {
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
@SerialName("First")
data class FirstEra(
    val startDate: Date,
    override val text: String,
    override val isPrefix: Boolean,
) : CalendarEra()

@Serializable
@SerialName("Later")
data class LaterEra(
    val startYear: Year,
    override val text: String,
    override val isPrefix: Boolean,
) : CalendarEra()