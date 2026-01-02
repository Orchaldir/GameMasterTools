package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.util.HasSimpleStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.reducer.time.validateDays
import at.orchaldir.gm.core.reducer.time.validateEras
import at.orchaldir.gm.core.reducer.time.validateHolidays
import at.orchaldir.gm.core.reducer.time.validateMonths
import at.orchaldir.gm.core.reducer.util.validateOrigin
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.core.selector.time.date.resolveDay
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable

const val CALENDAR_TYPE = "Calendar"
val ALLOWED_CALENDAR_ORIGINS = listOf(
    OriginType.Created,
    OriginType.Modified,
    OriginType.Undefined,
)

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
    val name: Name = Name.init(id),
    val days: Days = DayOfTheMonth,
    val months: Months = ComplexMonths(emptyList()),
    val eras: CalendarEras = CalendarEras(),
    val date: Date? = null,
    val origin: Origin = UndefinedOrigin,
    val defaultFormat: DateFormat = DateFormat(),
) : ElementWithSimpleName<CalendarId>, HasSimpleStartDate {

    init {
        validateOriginType(origin, ALLOWED_CALENDAR_ORIGINS)
    }

    override fun id() = id
    override fun name() = name.text
    override fun startDate() = date

    override fun validate(state: State) {
        validateDays(this)
        validateMonths(this)
        validateEras(state, this)
        validateOrigin(state, id, origin, null, ::CalendarId)
        validateHolidays(state, this)
    }

    // data

    fun getDaysPerYear() = months.getDaysPerYear()

    fun getMinDaysPerMonth() = months.getMinDaysPerMonth()

    fun getStartDateInDefaultCalendar() = eras.first.startDay

    fun getValidDaysPerWeek(): Int {
        val daysPerWeek = days.getDaysPerWeek()
        require(daysPerWeek > 0) { "Calendar ${id.value} doesn't support weeks!" }

        return daysPerWeek
    }

    fun getValidDateTypes() = if (days.hasWeeks()) {
        DateType.entries
    } else {
        DateType.entries - DateType.Week
    }.toSet() - DateType.DayRange

    // day

    fun getWeekDay(date: Day) = when (days) {
        DayOfTheMonth -> null
        is Weekdays -> date.day.modulo(days.weekDays.size)
    }

    // month

    fun getMonth(day: Day) = getMonth(resolveDay(day))

    fun getMonth(day: DisplayDay) = getMonth(day.month)
    fun getMonth(month: DisplayMonth) = months.getMonth(month.monthIndex)

    fun getMonthsPerYear() = months.getSize()
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

    fun isAfter(after: Date, before: Date) = compareTo(after, before) > 0
    fun isAfterOptional(after: Date?, b: Date?) = if (after != null && b != null) {
        compareTo(after, b) > 0
    } else {
        true
    }

    fun isAfterOrEqual(after: Date, before: Date) = compareTo(after, before) >= 0
    fun isAfterOrEqualOptional(after: Date?, before: Date?) = if (after != null && before != null) {
        compareTo(after, before) >= 0
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

    fun getYears(duration: Duration) = duration.days / getDaysPerYear()

}