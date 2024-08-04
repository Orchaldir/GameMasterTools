package at.orchaldir.gm.core.model.calendar.date

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Date

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date()

@Serializable
@SerialName("Original")
data class Year(val year: Int) : Date()