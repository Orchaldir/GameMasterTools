package at.orchaldir.gm.core.model.time.holiday

import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.time.calendar.CalendarId
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
    val name: Name = Name.init("Holiday ${id.value}"),
    val calendar: CalendarId = CalendarId(0),
    val relativeDate: RelativeDate = DayInYear(0, 0),
    val purpose: HolidayPurpose = Anniversary,
) : ElementWithSimpleName<HolidayId> {

    override fun id() = id
    override fun name() = name.text
}