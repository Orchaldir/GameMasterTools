package at.orchaldir.gm.core.model.calendar

import kotlinx.serialization.Serializable

@Serializable
data class MonthDefinition(
    val name: String,
    val days: Int = 30,
)