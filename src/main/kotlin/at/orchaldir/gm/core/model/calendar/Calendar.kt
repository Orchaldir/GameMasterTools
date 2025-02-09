package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
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
    val months: List<Month> = emptyList(),
    val eras: CalendarEras = CalendarEras("BC", true, Day(0), "AD", false),
    val origin: CalendarOrigin = OriginalCalendar,
) : ElementWithSimpleName<CalendarId> {

    override fun id() = id
    override fun name() = name

    // data

    fun display(date: DisplayDate) = eras.display(date)

    fun getDaysPerYear() = months.sumOf { it.days }

    fun getMinDaysPerMonth() = months.minOf { it.days }

    fun getStartDate() = eras.first.startDate

    fun getOffsetInDays() = when (eras.first.startDate) {
        is Day -> -eras.first.startDate.day
        is Year -> -eras.first.startDate.year * getDaysPerYear()
        is Decade -> -eras.first.startDate.decade * getDaysPerYear() * 10
    }

    fun getOffsetInYears() = getOffsetInDays() / getDaysPerYear()

    fun getOffsetInDecades() = getOffsetInYears() / 10

    // day

    fun getDay(date: Date) = when (date) {
        is Day -> date
        is Year -> getStartOfYear(date)
        is Decade -> getStartOfDecade(date)
    }

    fun getDisplayDay(date: Date): DisplayDay = when (date) {
        is Day -> resolve(date)
        is Year -> getDisplayStartOfYear(date)
        is Decade -> getDisplayStartOfDecade(date)
    }

    fun getWeekDay(date: Day) = when (days) {
        DayOfTheMonth -> null
        is Weekdays -> {
            val day = date.day + getOffsetInDays()

            day.modulo(days.weekDays.size)
        }
    }

    // month

    fun getMonth(day: Day) = getMonth(resolve(day))

    fun getMonth(day: DisplayDay) = months[day.monthIndex]

    fun getLastMonthIndex() = months.size - 1

    fun getStartOfMonth(day: Day) = getStartOfMonth(resolve(day))

    fun getStartOfMonth(day: DisplayDay) = resolve(day.copy(dayIndex = 0))

    fun getStartOfNextMonth(day: Day): Day {
        val displayDay = resolve(day)
        val startOfMonth = getStartOfMonth(displayDay)
        val month = getMonth(displayDay)

        return startOfMonth + month.days
    }

    fun getStartOfPreviousMonth(day: Day): Day {
        val displayDay = resolve(day)
        val startOfMonth = getStartOfMonth(displayDay)
        val month = getMonth(startOfMonth - 1)

        return startOfMonth - month.days
    }

    fun getEndOfMonth(day: Day) = getStartOfNextMonth(day) - 1

    // year

    fun getYear(date: Date): Year = when (date) {
        is Day -> resolve(resolve(date).year)
        is Year -> date
        is Decade -> resolve(resolve(date).year())
    }

    fun getDisplayYear(date: Date): DisplayYear = when (date) {
        is Day -> resolve(date).year
        is Year -> resolve(date)
        is Decade -> resolve(date).year()
    }

    fun getStartOfYear(year: Year) = resolve(getDisplayStartOfYear(year))
    fun getDisplayStartOfYear(year: Year) = getStartOfYear(resolve(year))

    fun getStartOfYear(year: DisplayYear) = DisplayDay(year, 0, 0, null)

    fun getEndOfYear(year: Year) = getStartOfYear(year.nextYear()).previousDay()

    // decade

    fun getDecade(date: Date): Decade = when (date) {
        is Day -> resolve(resolve(getYear(date)).decade())
        is Year -> resolve(resolve(date).decade())
        is Decade -> date
    }

    fun getDisplayDecade(date: Date): DisplayDecade = when (date) {
        is Day -> resolve(getYear(date)).decade()
        is Year -> resolve(date).decade()
        is Decade -> resolve(date)
    }

    fun getStartOfDecade(decade: Decade) = resolve(getDisplayStartOfDecade(decade))

    fun getDisplayStartOfDecade(decade: Decade) = getStartOfDecade(resolve(decade))

    fun getStartOfDecade(decade: DisplayDecade) = DisplayDay(decade.year(), 0, 0, null)

    fun getEndOfDecade(decade: Decade) = getStartOfDecade(decade.nextDecade()).previousDay()

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
    fun isAfterOrEqual(a: Date, b: Date) = compareTo(a, b) >= 0
    fun isAfterOrEqualOptional(a: Date?, b: Date?) = if (a != null && b != null) {
        compareTo(a, b) >= 0
    } else {
        true
    }

    // duration

    fun getDurationInYears(from: Date, to: Day) = getYears(getDuration(from, to))

    fun getDuration(from: Date, to: Day) = getDay(from).getDurationBetween(to)

    fun getYears(duration: Duration) = duration.day / getDaysPerYear()

}