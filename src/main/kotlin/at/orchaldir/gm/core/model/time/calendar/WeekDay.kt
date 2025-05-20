package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.util.name.Name
import kotlinx.serialization.Serializable

@Serializable
data class WeekDay(
    val name: Name,
)
