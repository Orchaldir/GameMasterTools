package at.orchaldir.gm.core.model.time.date

import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.time.date.getStartDayOfCentury
import at.orchaldir.gm.core.selector.time.date.getStartDayOfDecade
import at.orchaldir.gm.core.selector.time.date.getStartDayOfMonth
import at.orchaldir.gm.core.selector.time.date.getStartDayOfYear
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

enum class DateType {
    Day,
    Month,
    Year,
    Decade,
    Century,
}

@Serializable
sealed interface Date {

    fun getType() = when (this) {
        is Day -> DateType.Day
        is Month -> DateType.Month
        is Year -> DateType.Year
        is Decade -> DateType.Decade
        is Century -> DateType.Century
    }

    fun isBetween(calendar: Calendar, start: Day, end: Day): Boolean

    fun next(): Date
    fun previous(): Date

}

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date {
    operator fun compareTo(other: Day): Int {
        return day.compareTo(other.day)
    }

    override fun next() = nextDay()
    override fun previous() = previousDay()

    fun nextDay() = this + 1
    fun previousDay() = this - 1

    operator fun plus(duration: Int) = Day(day + duration)
    operator fun minus(duration: Int) = Day(day - duration)

    fun getDurationBetween(other: Day) = Duration((day - other.day).absoluteValue)
    override fun isBetween(calendar: Calendar, start: Day, end: Day) = day >= start.day && day <= end.day
}

@Serializable
@SerialName("Month")
data class Month(val month: Int) : Date {
    operator fun compareTo(other: Month): Int {
        return month.compareTo(other.month)
    }

    override fun next() = nextMonth()
    override fun previous() = previousMonth()

    fun nextMonth() = this + 1
    fun previousMonth() = this - 1

    operator fun plus(duration: Int) = Month(month + duration)
    operator fun minus(duration: Int) = Month(month - duration)

    fun getDurationBetween(other: Month) = Duration((month - other.month).absoluteValue)
    override fun isBetween(calendar: Calendar, start: Day, end: Day) = calendar
        .getStartDayOfMonth(this)
        .isBetween(calendar, start, end)
}

@Serializable
@SerialName("Year")
data class Year(val year: Int) : Date {

    override fun isBetween(calendar: Calendar, start: Day, end: Day) = calendar
        .getStartDayOfYear(this)
        .isBetween(calendar, start, end)

    override fun next() = nextYear()
    override fun previous() = previousYear()

    fun nextYear() = Year(year + 1)
    fun previousYear() = Year(year - 1)

    operator fun compareTo(other: Year): Int {
        return year.compareTo(other.year)
    }

}

@Serializable
@SerialName("Decade")
data class Decade(val decade: Int) : Date {

    override fun isBetween(calendar: Calendar, start: Day, end: Day) = calendar
        .getStartDayOfDecade(this)
        .isBetween(calendar, start, end)

    override fun next() = nextDecade()
    override fun previous() = previousDecade()

    fun nextDecade() = Decade(decade + 1)
    fun previousDecade() = Decade(decade - 1)

    operator fun compareTo(other: Decade): Int {
        return decade.compareTo(other.decade)
    }

}

@Serializable
@SerialName("Century")
data class Century(val century: Int) : Date {

    override fun isBetween(calendar: Calendar, start: Day, end: Day) = calendar
        .getStartDayOfCentury(this)
        .isBetween(calendar, start, end)

    override fun next() = nextCentury()
    override fun previous() = previousCentury()

    fun nextCentury() = Century(century + 1)
    fun previousCentury() = Century(century - 1)

    operator fun compareTo(other: Century): Int {
        return century.compareTo(other.century)
    }

}