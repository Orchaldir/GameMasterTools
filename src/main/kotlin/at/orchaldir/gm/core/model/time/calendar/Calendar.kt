package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.core.selector.time.date.resolveDay
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable

const val CALENDAR_TYPE = "Calendar"

@JvmInline
@Serializable
value class CalendarId(val value: Int) : Id<CalendarId> {

    override fun next() = CalendarId(value + 1)
    override fun type() = CALENDAR_TYPE
    override fun value() = value

}

@Serializable
data class Calendar(
    val id: CalendarId,
    val name: String = "Calendar ${id.value}",
    val days: Days = DayOfTheMonth,
    val months: Months = ComplexMonths(emptyList()),
    val eras: CalendarEras = CalendarEras("BC", true, Day(0), "AD", false),
    val origin: CalendarOrigin = OriginalCalendar,
    val defaultFormat: DateFormat = DateFormat(),
) : ElementWithSimpleName<CalendarId> {

    override fun id() = id
    override fun name() = name

    // data

    fun getDaysPerYear() = months.getDaysPerYear()

    fun getMinDaysPerMonth() = months.getMinDaysPerMonth()

    fun getStartDate() = eras.first.startDate

    fun getOffsetInDays() = when (eras.first.startDate) {
        is Day -> -eras.first.startDate.day
        is Year -> -eras.first.startDate.year * getDaysPerYear()
        is Decade -> -eras.first.startDate.decade * getDaysPerYear() * 10
        is Century -> -eras.first.startDate.century * getDaysPerYear() * 100
    }

    fun getOffsetInYears() = getOffsetInDays() / getDaysPerYear()

    fun getOffsetInDecades() = getOffsetInYears() / 10

    fun getOffsetInCenturies() = getOffsetInYears() / 100

    // day

    fun getWeekDay(date: Day) = when (days) {
        DayOfTheMonth -> null
        is Weekdays -> {
            val day = date.day + getOffsetInDays()

            day.modulo(days.weekDays.size)
        }
    }

    // month

    fun getMonth(day: Day) = getMonth(resolveDay(day))

    fun getMonth(day: DisplayDay) = months.getMonth(day.monthIndex)

    fun getLastMonthIndex() = months.getSize() - 1

    // compare dates

    fun compareToOptional(a: Date?, b: Date?): Int = if (a != null && b != null) {
        compareTo(a, b)
    } else if (a != null) {
        1
    } else if (b != null) {
        -1
    } else {
        0
    }

    fun compareTo(a: Date, b: Date) = getStartDay(a).compareTo(getStartDay(b))

    fun isAfter(a: Date, b: Date) = compareTo(a, b) > 0
    fun isAfterOptional(a: Date?, b: Date?) = if (a != null && b != null) {
        compareTo(a, b) > 0
    } else {
        true
    }

    fun isAfterOrEqual(a: Date, b: Date) = compareTo(a, b) >= 0
    fun isAfterOrEqualOptional(a: Date?, b: Date?) = if (a != null && b != null) {
        compareTo(a, b) >= 0
    } else {
        true
    }

    fun max(a: Date, b: Date?) = if (b != null) {
        when (compareTo(a, b)) {
            1 -> a
            else -> b
        }
    } else a

    fun maxOptional(a: Date?, b: Date?) = if (a != null && b != null) {
        when (compareTo(a, b)) {
            1 -> a
            else -> b
        }
    } else a ?: b

    // duration

    fun getDurationInYears(from: Date, to: Day) = getYears(getDuration(from, to))

    fun getDuration(from: Date, to: Day) = getStartDay(from).getDurationBetween(to)

    fun getYears(duration: Duration) = duration.day / getDaysPerYear()

}