package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarEra(
    val countFrom: Boolean,
    val text: String,
    val isPrefix: Boolean,
)
