package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue


@JvmInline
@Serializable
value class CalendarId(val value: Int) : Id<CalendarId> {

    override fun next() = CalendarId(value + 1)
    override fun value() = value

}

@Serializable
data class Calendar(
    val id: CalendarId,
    val name: String = "Calendar ${id.value}",
    val days: Days = DayOfTheMonth,
    val months: List<Month> = emptyList(),
    val offsetInDays: Int = 0,
    val origin: CalendarOrigin = OriginalCalendar,
    val creationDate: Date = Year(0),
) : Element<CalendarId> {

    override fun id() = id

    fun getDaysPerYear() = months.sumOf { it.days }

    fun resolve(date: Date) = when (date) {
        is Day -> resolve(date)
        is Year -> resolve(date)
    }

    private fun resolve(date: Day): CalendarDay {
        val daysPerYear = getDaysPerYear()
        val day = date.day + offsetInDays

        if (day >= 0) {
            val year = day / daysPerYear
            var remainingDays = day % daysPerYear

            for ((monthIndex, monthData) in months.withIndex()) {
                if (remainingDays < monthData.days) {
                    return CalendarDay(year, monthIndex, remainingDays)
                }

                remainingDays -= monthData.days
            }

            error("Unreachable")
        }

        val absoluteDate = day.absoluteValue - 1
        val year = -(1 + absoluteDate / daysPerYear)
        var remainingDays = absoluteDate % daysPerYear

        for ((monthIndex, monthData) in months.withIndex().reversed()) {
            if (remainingDays < monthData.days) {
                val dayIndex = monthData.days - remainingDays - 1
                return CalendarDay(year, monthIndex, dayIndex)
            }

            remainingDays -= monthData.days
        }

        error("Unreachable")
    }

    private fun resolve(date: Year): CalendarYear {
        val offsetInYears = offsetInDays / getDaysPerYear()
        val year = date.year + offsetInYears

        return CalendarYear(year)
    }

    fun resolve(date: CalendarDate) = when (date) {
        is CalendarDay -> resolve(date)
        is CalendarYear -> resolve(date)
    }

    private fun resolve(date: CalendarDay): Day {
        val daysPerYear = getDaysPerYear()

        if (date.yearIndex >= 0) {
            var day = date.yearIndex * daysPerYear + date.dayIndex - offsetInDays

            (0..<date.monthIndex).map { months[it] }
                .forEach { day += it.days }

            return Day(day)
        }

        var day = (date.yearIndex + 1) * daysPerYear - offsetInDays

        (date.monthIndex..<months.size).map { months[it] }
            .forEach { day -= it.days }

        day += date.dayIndex

        return Day(day)
    }

    fun resolve(date: CalendarYear): Year {
        val offsetInYears = offsetInDays / getDaysPerYear()
        val year = date.year - offsetInYears

        return Year(year)
    }
}