package at.orchaldir.gm.core.model.time

import at.orchaldir.gm.core.model.time.calendar.CalendarId
import kotlinx.serialization.Serializable

@Serializable
data class Time(
    val defaultCalendar: CalendarId = CalendarId(0),
    val currentDate: Day = Day(0),
)
