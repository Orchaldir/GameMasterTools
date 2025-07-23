package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.util.name.Name
import kotlinx.serialization.Serializable

@Serializable
data class MonthDefinition(
    val name: Name,
    val days: Int = 30,
) {
    constructor(days: Int, name: String) : this(Name.init(name), days)

    fun isInside(dayIndex: Int) = dayIndex in 0..<days
}
