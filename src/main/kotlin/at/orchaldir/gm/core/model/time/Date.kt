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

    abstract fun isBetween(start: Day, end: Day): Boolean

}

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date() {
    operator fun compareTo(other: Day): Int {
        return day.compareTo(other.day)
    }

    fun next() = this + 1
    fun previous() = this - 1

    operator fun plus(duration: Int) = Day(day + duration)
    operator fun minus(duration: Int) = Day(day - duration)

    fun getDurationBetween(other: Day) = Duration((day - other.day).absoluteValue)

    override fun isBetween(start: Day, end: Day) = day >= start.day && day <= end.day
}

@Serializable
@SerialName("Year")
data class Year(val year: Int) : Date() {

    override fun isBetween(start: Day, end: Day) = false

    fun next() = Year(year + 1)
    fun previous() = Year(year - 1)

}