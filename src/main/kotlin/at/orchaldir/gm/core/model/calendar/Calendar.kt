package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.utils.Element
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
) : Element<CalendarId> {

    override fun id() = id
    override fun name() = name

    fun getDaysPerYear() = months.sumOf { it.days }

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
    }

    fun getYears(duration: Duration) = duration.day / getDaysPerYear()

    fun display(date: DisplayDate) = eras.display(date)

    fun resolve(date: Date) = when (date) {
        is Day -> resolve(date)
        is Year -> resolve(date)
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
        val offsetInYears = getOffsetInDays() / getDaysPerYear()
        val year = date.year + offsetInYears

        if (year >= 0) {
            return DisplayYear(1, year)
        }

        return DisplayYear(0, -(year + 1))
    }

    fun resolve(date: DisplayDate) = when (date) {
        is DisplayDay -> resolve(date)
        is DisplayYear -> resolve(date)
    }

    fun resolve(date: DisplayDay): Day {
        val daysPerYear = getDaysPerYear()
        val offsetInDays = getOffsetInDays()

        if (date.eraIndex == 1) {
            var day = date.yearIndex * daysPerYear + date.dayIndex - offsetInDays

            (0..<date.monthIndex).map { months[it] }
                .forEach { day += it.days }

            return Day(day)
        }

        var day = -date.yearIndex * daysPerYear - offsetInDays

        (date.monthIndex..<months.size).map { months[it] }
            .forEach { day -= it.days }

        day += date.dayIndex

        return Day(day)
    }

    fun resolve(date: DisplayYear): Year {
        val offsetInYears = getOffsetInDays() / getDaysPerYear()

        if (date.eraIndex == 1) {
            val year = date.yearIndex - offsetInYears

            return Year(year)
        }

        val year = -(date.yearIndex + 1) - offsetInYears

        return Year(year)
    }
}