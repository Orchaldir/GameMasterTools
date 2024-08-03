package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.CalendarDay
import at.orchaldir.gm.core.model.calendar.date.Date
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable


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
    val origin: CalendarOrigin = OriginalCalendar,
) : Element<CalendarId> {

    override fun id() = id

    fun getDaysPerYear() = months.sumOf { it.days }

    fun resolve(date: Date): CalendarDay {
        val daysPerYear = getDaysPerYear()

        if (date.day >= 0) {
            val year = date.day / daysPerYear;
            var remainingDays = date.day % daysPerYear;

            for ((index, data) in months.withIndex()) {
                if (remainingDays < data.days) {
                    return CalendarDay(year, index, remainingDays)
                }

                remainingDays -= data.days
            }

            error("Unreachable")
        }

        TODO()
    }

}