package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.util.name.Name
import kotlinx.serialization.Serializable

@Serializable
data class MonthDefinition(
    val name: Name,
    val days: Int = 30,
) {

    fun isInside(dayIndex: Int) = dayIndex in 0..<days
}
