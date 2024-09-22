package at.orchaldir.gm.core.model.time

import at.orchaldir.gm.core.model.calendar.Calendar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
sealed class Date {

    fun getType() = when (this) {
        is Day -> DateType.Day
        is Year -> DateType.Year
    }

    abstract fun isBetween(calendar: Calendar, start: Day, end: Day): Boolean

    abstract fun next(): Date

}

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date() {
    operator fun compareTo(other: Day): Int {
        return day.compareTo(other.day)
    }

    override fun next() = nextDay()
    fun nextDay() = this + 1
    fun previousDay() = this - 1

    operator fun plus(duration: Int) = Day(day + duration)
    operator fun minus(duration: Int) = Day(day - duration)

    fun getDurationBetween(other: Day) = Duration((day - other.day).absoluteValue)
    override fun isBetween(calendar: Calendar, start: Day, end: Day) = day >= start.day && day <= end.day
}

@Serializable
@SerialName("Year")
data class Year(val year: Int) : Date() {

    override fun isBetween(calendar: Calendar, start: Day, end: Day) = calendar
        .getStartOfYear(this)
        .isBetween(calendar, start, end)

    override fun next() = nextYear()
    fun nextYear() = Year(year + 1)
    fun previousYear() = Year(year - 1)

}