package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CalendarOrigin

@Serializable
@SerialName("Improved")
data class ImprovedCalendar(val parent: CalendarId) : CalendarOrigin()

@Serializable
@SerialName("Original")
data object OriginalCalendar : CalendarOrigin()
