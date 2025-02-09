package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CalendarOriginType {
    Improved,
    Original,
}

@Serializable
sealed class CalendarOrigin {

    fun getType() = when (this) {
        is ImprovedCalendar -> CalendarOriginType.Improved
        OriginalCalendar -> CalendarOriginType.Original
    }

}

@Serializable
@SerialName("Improved")
data class ImprovedCalendar(val parent: CalendarId) : CalendarOrigin()

@Serializable
@SerialName("Original")
data object OriginalCalendar : CalendarOrigin()
