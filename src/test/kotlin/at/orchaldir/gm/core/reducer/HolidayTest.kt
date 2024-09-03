package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.holiday.FixedDayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.holiday.WeekdayInMonth
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = HolidayId(0)
private val CALENDAR_ID0 = CalendarId(0)
private val CALENDAR_ID1 = CalendarId(1)
private val WEEKDAYS = Weekdays(listOf(WeekDay("d0"), WeekDay("d1")))
private val MONTHS = listOf(MonthDefinition("M0", 2), MonthDefinition("M1", 3))
private val CALENDAR0 = Calendar(CALENDAR_ID0, "C0", WEEKDAYS, months = MONTHS)
private val CALENDAR1 = Calendar(CALENDAR_ID1, months = MONTHS)

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

        @Nested
        inner class FixedDayInYearTest {

            @Test
            fun `In unknown month`() {
                val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                val holiday = Holiday(ID0, relativeDate = FixedDayInYear(0, 2))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Outside first month`() {
                val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                val holiday = Holiday(ID0, relativeDate = FixedDayInYear(2, 0))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Outside second month`() {
                val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                val holiday = Holiday(ID0, relativeDate = FixedDayInYear(3, 1))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid fixed days`() {
                CALENDAR0.months.withIndex().forEach { month ->
                    repeat(month.value.days) { day ->
                        val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                        val holiday = Holiday(ID0, relativeDate = FixedDayInYear(day, month.index))
                        val action = UpdateHoliday(holiday)

                        assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(ID0))
                    }

                }
            }
        }

        @Nested
        inner class WeekdayInMonthTest {

            @Test
            fun `In unknown month`() {
                val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                val holiday = Holiday(ID0, relativeDate = WeekdayInMonth(0, 0, 2))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Calendar without weekdays is unsupported`() {
                val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR1)))
                val holiday = Holiday(ID0, calendar = CALENDAR_ID1, relativeDate = WeekdayInMonth(0, 0, 0))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalStateException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown weekday`() {
                val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                val holiday = Holiday(ID0, relativeDate = WeekdayInMonth(3, 0, 0))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid weekday`() {
                repeat(WEEKDAYS.weekDays.size) { day ->
                    val state = State(listOf(Storage(Holiday(ID0)), Storage(CALENDAR0)))
                    val holiday = Holiday(ID0, relativeDate = WeekdayInMonth(day, 0, 0))
                    val action = UpdateHoliday(holiday)

                    assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(ID0))
                }
            }

        }
    }

}