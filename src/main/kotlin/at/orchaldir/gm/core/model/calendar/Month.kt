package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.Serializable

@Serializable
data class Month(
    val name: String,
    val days: Int = 30,
) {

    fun isInside(dayIndex: Int) = dayIndex in 0..<days
}
