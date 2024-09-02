package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.calendar.WeekDay
import at.orchaldir.gm.core.model.holiday.FixedDayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.holiday.RelativeDateType
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = HolidayId(0)
private val CALENDAR_ID0 = CalendarId(0)
private val CALENDAR_ID1 = CalendarId(1)
private val CALENDAR0 = Calendar(
    CALENDAR_ID0, "C0", Weekdays(listOf(WeekDay("d0"), WeekDay("d1"))),
    months = listOf(MonthDefinition("M0", 2), MonthDefinition("M1", 3))
)
private val CALENDAR1 = Calendar(CALENDAR_ID1)

class HolidayTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing holiday`() {
            val state = State(Storage(Holiday(ID0)))
            val action = DeleteHoliday(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getHolidayStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteHoliday(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateHoliday(Holiday(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot update with unknown calendar`() {
            val state = State(Storage(Holiday(ID0)))
            val holiday = Holiday(ID0, "Test")
            val action = UpdateHoliday(holiday)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Fixed day in unknown month`() {
            val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
            val holiday = Holiday(ID0, relativeDate = FixedDayInYear(0, 2))
            val action = UpdateHoliday(holiday)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Fixed day is outside first month`() {
            val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
            val holiday = Holiday(ID0, relativeDate = FixedDayInYear(2, 0))
            val action = UpdateHoliday(holiday)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Fixed day is outside second month`() {
            val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
            val holiday = Holiday(ID0, relativeDate = FixedDayInYear(3, 1))
            val action = UpdateHoliday(holiday)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Valid fixed days`() {
            val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
            val holiday = Holiday(ID0, "Test")
            val action = UpdateHoliday(holiday)

            assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(ID0))
        }
    }

}