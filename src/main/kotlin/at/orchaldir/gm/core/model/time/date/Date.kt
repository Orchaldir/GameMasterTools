package at.orchaldir.gm.core.model.time.date

import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.date.getStartDay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

enum class DateType {
    Day,
    DayRange,
    Week,
    Month,
    Year,
    Decade,
    Century,
    Millennium,
}

@Serializable
sealed interface Date {

    fun getType() = when (this) {
        is Day -> DateType.Day
        is DayRange -> DateType.DayRange
        is Week -> DateType.Week
        is Month -> DateType.Month
        is Year -> DateType.Year
        is Decade -> DateType.Decade
        is Century -> DateType.Century
        is Millennium -> DateType.Millennium
    }

    fun next(): Date?
    fun previous(): Date?

    fun isOverlapping(calendar: Calendar, date: Date): Boolean {
        val start = calendar.getStartDay(date)
        val end = calendar.getEndDay(date)

        return isOverlapping(calendar, start, end)
    }

    fun isOverlapping(calendar: Calendar, start: Day, end: Day): Boolean {
        val currentStart = calendar.getStartDay(this)
        val currentEnd = calendar.getEndDay(this)

        return !isNotBetween(currentStart, currentEnd, start, end)
    }
}

private fun isNotBetween(
    startA: Day,
    endA: Day,
    startB: Day,
    endB: Day,
) = startB > endA || endB < startA

@Serializable
@SerialName("Day")
data class Day(val day: Int) : Date {
    override fun next() = nextDay()
    override fun previous() = previousDay()

    fun nextDay() = this + 1
    fun previousDay() = this - 1

    operator fun plus(duration: Int) = Day(day + duration)
    operator fun minus(duration: Int) = Day(day - duration)

    operator fun compareTo(other: Day): Int {
        return day.compareTo(other.day)
    }

    fun getDurationBetween(other: Day) = Duration((day - other.day).absoluteValue)
    override fun isOverlapping(calendar: Calendar, start: Day, end: Day) = day >= start.day && day <= end.day
}

@Serializable
@SerialName("DayRange")
data class DayRange(
    val startDay: Day,
    val endDay: Day,
) : Date {
    constructor(start: Int, end: Int) : this(Day(start), Day(end))

    override fun next() = null
    override fun previous() = null

    override fun isOverlapping(calendar: Calendar, start: Day, end: Day) =
        !isNotBetween(start, end)

    fun isNotBetween(start: Day, end: Day) = isNotBetween(startDay, endDay, end, start)
}

@Serializable
@SerialName("Week")
data class Week(val week: Int) : Date {
    override fun next() = nextWeek()
    override fun previous() = previousWeek()

    fun nextWeek() = this + 1
    fun previousWeek() = this - 1

    operator fun plus(duration: Int) = Week(week + duration)
    operator fun minus(duration: Int) = Week(week - duration)

    operator fun compareTo(other: Week): Int {
        return week.compareTo(other.week)
    }
}

@Serializable
@SerialName("Month")
data class Month(val month: Int) : Date {
    override fun next() = nextMonth()
    override fun previous() = previousMonth()

    fun nextMonth() = this + 1
    fun previousMonth() = this - 1

    operator fun plus(duration: Int) = Month(month + duration)
    operator fun minus(duration: Int) = Month(month - duration)

    operator fun compareTo(other: Month): Int {
        return month.compareTo(other.month)
    }
}

@Serializable
@SerialName("Year")
data class Year(val year: Int) : Date {

    override fun next() = nextYear()
    override fun previous() = previousYear()

    fun nextYear() = Year(year + 1)
    fun previousYear() = Year(year - 1)

    operator fun plus(duration: Int) = Year(year + duration)
    operator fun minus(duration: Int) = Year(year - duration)

    operator fun compareTo(other: Year): Int {
        return year.compareTo(other.year)
    }
}

@Serializable
@SerialName("Decade")
data class Decade(val decade: Int) : Date {

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

    override fun next() = nextCentury()
    override fun previous() = previousCentury()

    fun nextCentury() = Century(century + 1)
    fun previousCentury() = Century(century - 1)

    operator fun compareTo(other: Century): Int {
        return century.compareTo(other.century)
    }
}

@Serializable
@SerialName("Millennium")
data class Millennium(val millennium: Int) : Date {

    override fun next() = nextMillennium()
    override fun previous() = previousMillennium()

    fun nextMillennium() = Millennium(millennium + 1)
    fun previousMillennium() = Millennium(millennium - 1)

    operator fun compareTo(other: Millennium): Int {
        return millennium.compareTo(other.millennium)
    }
}