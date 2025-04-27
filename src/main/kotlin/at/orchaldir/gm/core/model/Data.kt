package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Day
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val time: Time = Time(),
)
