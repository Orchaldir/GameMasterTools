package at.orchaldir.gm.core.model.time

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
sealed class Date {

    fun getType() = when (this) {
        is Day -> DateType.Day
        is Year -> DateType.Year
    }

}

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date() {
    operator fun compareTo(other: Day): Int {
        return day.compareTo(other.day)
    }

    fun getDurationBetween(other: Day) = Duration((day - other.day).absoluteValue)
}

@Serializable
@SerialName("Year")
data class Year(val year: Int) : Date()