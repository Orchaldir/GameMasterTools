package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val HOLIDAY_TYPE = "Holiday"

@JvmInline
@Serializable
value class HolidayId(val value: Int) : Id<HolidayId> {

    override fun next() = HolidayId(value + 1)
    override fun type() = HOLIDAY_TYPE
    override fun value() = value

}

@Serializable
data class Holiday(
    val id: HolidayId,
    val name: String = "Holiday ${id.value}",
    val calendar: CalendarId = CalendarId(0),
    val relativeDate: RelativeDate = FixedDayInYear(0, 0),
) : ElementWithSimpleName<HolidayId> {

    override fun id() = id
    override fun name() = name
}