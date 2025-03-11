package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.selector.time.date.*
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

    fun getDay(date: Date) = when (date) {
        is Day -> date
        is Year -> getStartOfYear(date)
        is Decade -> getStartOfDecade(date)
        is Century -> getStartOfCentury(date)
    }

    fun getDisplayDay(date: Date): DisplayDay = when (date) {
        is Day -> resolveDay(date)
        is Year -> getDisplayStartOfYear(date)
        is Decade -> getDisplayStartOfDecade(date)
        is Century -> getDisplayStartOfCentury(date)
    }

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

    // year

    fun getYear(date: Date): Year = when (date) {
        is Day -> resolveYear(resolveDay(date).year)
        is Year -> date
        is Decade -> resolveYear(resolveDecade(date).year())
        is Century -> resolveYear(resolveCentury(date).year())
    }

    fun getDisplayYear(date: Date): DisplayYear = when (date) {
        is Day -> resolveDay(date).year
        is Year -> resolveYear(date)
        is Decade -> resolveDecade(date).year()
        is Century -> resolveCentury(date).year()
    }

    // decade

    fun getDecade(date: Date): Decade = when (date) {
        is Day -> resolveDecade(resolveYear(getYear(date)).decade())
        is Year -> resolveDecade(resolveYear(date).decade())
        is Decade -> date
        is Century -> resolveDecade(resolveCentury(date).year().decade())
    }

    fun getDisplayDecade(date: Date): DisplayDecade = when (date) {
        is Day -> resolveYear(getYear(date)).decade()
        is Year -> resolveYear(date).decade()
        is Decade -> resolveDecade(date)
        is Century -> resolveCentury(date).year().decade()
    }

    fun getStartOfDecade(decade: Decade) = resolveDay(getDisplayStartOfDecade(decade))

    fun getDisplayStartOfDecade(decade: Decade) = getStartOfDecade(resolveDecade(decade))

    fun getStartOfDecade(decade: DisplayDecade) = DisplayDay(decade.year(), 0, 0, null)

    fun getEndOfDecade(decade: Decade) = getStartOfDecade(decade.nextDecade()).previousDay()

    // century

    fun getCentury(date: Date): Century = when (date) {
        is Day -> resolveCentury(resolveYear(getYear(date)).decade().century())
        is Year -> resolveCentury(resolveYear(date).decade().century())
        is Decade -> resolveCentury(resolveDecade(date).century())
        is Century -> date
    }

    fun getDisplayCentury(date: Date): DisplayCentury = when (date) {
        is Day -> resolveYear(getYear(date)).decade().century()
        is Year -> resolveYear(date).decade().century()
        is Decade -> resolveDecade(date).century()
        is Century -> resolveCentury(date)
    }

    fun getStartOfCentury(century: Century) = resolveDay(getDisplayStartOfCentury(century))

    fun getDisplayStartOfCentury(century: Century) = getStartOfCentury(resolveCentury(century))

    fun getStartOfCentury(century: DisplayCentury) = DisplayDay(century.year(), 0, 0, null)

    fun getEndOfCentury(century: Century) = getStartOfCentury(century.nextDecade()).previousDay()

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

    fun compareTo(a: Date, b: Date) = getDay(a).compareTo(getDay(b))

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

    fun getDuration(from: Date, to: Day) = getDay(from).getDurationBetween(to)

    fun getYears(duration: Duration) = duration.day / getDaysPerYear()

}