package at.orchaldir.gm.core.model.time.holiday

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.time.validateHolidayPurpose
import at.orchaldir.gm.core.reducer.time.validateRelativeDate
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
    val name: Name = Name.init(id),
    val calendar: CalendarId = CalendarId(0),
    val relativeDate: RelativeDate = DayInYear(0, 0),
    val purpose: HolidayPurpose = Anniversary,
) : ElementWithSimpleName<HolidayId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        val calendar = state.getCalendarStorage().getOrThrow(calendar)

        validateHolidayPurpose(state, purpose)
        validateRelativeDate(calendar, relativeDate)
    }

}