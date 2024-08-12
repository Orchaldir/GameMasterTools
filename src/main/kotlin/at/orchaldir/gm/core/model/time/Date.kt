package at.orchaldir.gm.core.model.time

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Date {

    fun getType() = when (this) {
        is Day -> DateType.Day
        is Year -> DateType.Year
    }

}

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date()

@Serializable
@SerialName("Year")
data class Year(val year: Int) : Date()