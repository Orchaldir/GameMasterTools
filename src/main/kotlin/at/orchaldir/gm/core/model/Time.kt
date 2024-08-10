package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.date.Day
import kotlinx.serialization.Serializable

@Serializable
data class Time(
    val defaultCalendar: CalendarId = CalendarId(0),
    val currentDate: Day = Day(0),
)
