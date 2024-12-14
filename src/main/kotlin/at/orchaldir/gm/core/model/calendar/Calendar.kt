package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

const val CALENDAR = "Calendar"

@JvmInline
@Serializable
value class CalendarId(val value: Int) : Id<CalendarId> {

    override fun next() = CalendarId(value + 1)
    override fun type() = CALENDAR
    override fun value() = value

}

@Serializable
data class Calendar(
    val id: CalendarId,
    val name: String = "Calendar ${id.value}",
    val days: Days = DayOfTheMonth,
    val months: List<MonthDefinition> = emptyList(),
    val eras: CalendarEras = CalendarEras("BC", true, Day(0), "AD", false),
    val origin: CalendarOrigin = OriginalCalendar,
) : ElementWithSimpleName<CalendarId> {

    override fun id() = id
    override fun name() = name

    fun getDaysPerYear() = months.sumOf { it.days }

    // month

    fun getMonth(day: Day) = getMonth(resolve(day))

    fun getMonth(day: DisplayDay) = months[day.monthIndex]

    fun getLastMonthIndex() = months.size - 1

    fun getStartDate() = eras.first.startDate

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

    fun getStartOfYear(year: Year) = resolve(getStartOfYear(resolve(year)))

    fun getStartOfYear(year: DisplayYear) = DisplayDay(year, 0, 0, null)

    fun getEndOfYear(year: Year) = getStartOfYear(year.nextYear()).previousDay()

    // decade

    fun getStartOfDecade(decade: Decade) = resolve(getStartOfDecade(resolve(decade)))

    fun getStartOfDecade(decade: DisplayDecade) = DisplayDay(decade.year(), 0, 0, null)

    fun getEndOfDecade(decade: Decade) = getStartOfDecade(decade.nextDecade()).previousDay()

    //

    fun compareToOptional(a: Date?, b: Date?): Int = if (a != null && b != null) {
        compareTo(a, b)
    } else {
        0
    }

    fun compareTo(a: Date, b: Date) = getDay(a).compareTo(getDay(b))

    fun isAfter(a: Date, b: Date) = compareTo(a, b) > 0
    fun isAfterOrEqual(a: Date, b: Date) = compareTo(a, b) >= 0

    fun getDay(date: Date) = when (date) {
        is Day -> date
        is Year -> getStartOfYear(date)
        is Decade -> getStartOfDecade(date)
    }

    fun getDurationInYears(from: Date, to: Day) = getYears(getDuration(from, to))

    fun getDuration(from: Date, to: Day) = getDay(from).getDurationBetween(to)

    fun getWeekDay(date: Day) = when (days) {
        DayOfTheMonth -> null
        is Weekdays -> {
            val day = date.day + getOffsetInDays()

            day.modulo(days.weekDays.size)
        }
    }

    private fun getOffsetInDays() = when (eras.first.startDate) {
        is Day -> -eras.first.startDate.day
        is Year -> -eras.first.startDate.year * getDaysPerYear()
        is Decade -> -eras.first.startDate.decade * getDaysPerYear() * 10
    }

    private fun getOffsetInYears() = getOffsetInDays() / getDaysPerYear()

    private fun getOffsetInDecades() = getOffsetInYears() / 10

    fun getYears(duration: Duration) = duration.day / getDaysPerYear()

    fun display(date: DisplayDate) = eras.display(date)

    fun resolve(date: Date) = when (date) {
        is Day -> resolve(date)
        is Year -> resolve(date)
        is Decade -> resolve(date)
    }

    fun resolve(date: Day): DisplayDay {
        val daysPerYear = getDaysPerYear()
        val day = date.day + getOffsetInDays()
        val weekdayIndex = getWeekDay(date)

        if (day >= 0) {
            val year = day / daysPerYear
            val remainingDays = day % daysPerYear
            val (monthIndex, dayIndex) = resolveDayAndMonth(remainingDays)

            return DisplayDay(1, year, monthIndex, dayIndex, weekdayIndex)
        }

        val absoluteDate = day.absoluteValue - 1
        val year = absoluteDate / daysPerYear
        var remainingDays = absoluteDate % daysPerYear

        for ((monthIndex, monthData) in months.withIndex().reversed()) {
            if (remainingDays < monthData.days) {
                val dayIndex = monthData.days - remainingDays - 1
                return DisplayDay(0, year, monthIndex, dayIndex, weekdayIndex)
            }

            remainingDays -= monthData.days
        }

        error("Unreachable")
    }

    fun resolveDayAndMonth(dayInYear: Int): Pair<Int, Int> {
        var remainingDays = dayInYear

        for ((monthIndex, monthData) in months.withIndex()) {
            if (remainingDays < monthData.days) {
                return Pair(monthIndex, remainingDays)
            }

            remainingDays -= monthData.days
        }

        error("Unreachable")
    }

    fun resolve(date: Year): DisplayYear {
        val offsetInYears = getOffsetInYears()
        val year = date.year + offsetInYears

        if (year >= 0) {
            return DisplayYear(1, year)
        }

        return DisplayYear(0, -(year + 1))
    }

    fun resolve(date: Decade): DisplayDecade {
        val offsetInDecades = getOffsetInDecades()
        val decade = date.decade + offsetInDecades

        if (decade >= 0) {
            return DisplayDecade(1, decade)
        }

        return DisplayDecade(0, -decade)
    }

    fun resolve(date: DisplayDate) = when (date) {
        is DisplayDay -> resolve(date)
        is DisplayYear -> resolve(date)
        is DisplayDecade -> resolve(date)
    }

    fun resolve(day: DisplayDay): Day {
        val daysPerYear = getDaysPerYear()
        val offsetInDays = getOffsetInDays()

        if (day.year.eraIndex == 1) {
            var dayIndex = day.year.yearIndex * daysPerYear + day.dayIndex - offsetInDays

            (0..<day.monthIndex).map { months[it] }
                .forEach { dayIndex += it.days }

            return Day(dayIndex)
        }

        var dayIndex = -day.year.yearIndex * daysPerYear - offsetInDays

        (day.monthIndex..<months.size).map { months[it] }
            .forEach { dayIndex -= it.days }

        dayIndex += day.dayIndex

        return Day(dayIndex)
    }

    fun resolve(date: DisplayYear): Year {
        val offsetInYears = getOffsetInYears()

        if (date.eraIndex == 1) {
            val year = date.yearIndex - offsetInYears

            return Year(year)
        }

        val year = -(date.yearIndex + 1) - offsetInYears

        return Year(year)
    }

    fun resolve(date: DisplayDecade): Decade {
        val offsetInDecades = getOffsetInDecades()

        if (date.eraIndex == 1) {
            val decade = date.decadeIndex - offsetInDecades

            return Decade(decade)
        }

        val decade = -date.decadeIndex - offsetInDecades

        return Decade(decade)
    }
}