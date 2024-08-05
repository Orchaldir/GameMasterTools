package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarEra(
    val countFrom: Boolean,
    val text: String,
    val isPrefix: Boolean,
) {
    fun resolve(year: Int) = resolve(year.toString())

    fun resolve(date: String) = if (isPrefix) {
        "$text $date"
    } else {
        "$date $text"
    }
}
