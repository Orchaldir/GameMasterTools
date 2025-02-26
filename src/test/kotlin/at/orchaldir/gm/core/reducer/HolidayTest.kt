package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.holiday.DayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.WeekdayInMonth
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HolidayTest {

    private val weekdays = Weekdays(listOf(WeekDay("d0"), WeekDay("d1")))
    private val months = ComplexMonths(listOf(Month("M0", 2), Month("M1", 3)))
    private val calendar0 = Calendar(CALENDAR_ID_0, "C0", weekdays, months = months)
    private val calendar1 = Calendar(CALENDAR_ID_1, months = months)
    private val state = State(
        listOf(
            Storage(listOf(calendar0, calendar1)),
            Storage(Holiday(HOLIDAY_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteHoliday(HOLIDAY_ID_0)

        @Test
        fun `Can delete an existing holiday`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getHolidayStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteHoliday(UNKNOWN_HOLIDAY_ID)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a culture`() {
            val culture = Culture(CultureId(0), holidays = setOf(HOLIDAY_ID_0))
            val newState = state.updateStorage(Storage(culture))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(newState, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateHoliday(Holiday(UNKNOWN_HOLIDAY_ID))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot update with unknown calendar`() {
            val holiday = Holiday(UNKNOWN_HOLIDAY_ID, "Test")
            val action = UpdateHoliday(holiday)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class DayInYearTest {

            @Test
            fun `In unknown month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(0, 2))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Outside first month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(2, 0))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Outside second month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(3, 1))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid fixed days`() {
                months.months.withIndex().forEach { month ->
                    repeat(month.value.days) { day ->
                        val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(day, month.index))
                        val action = UpdateHoliday(holiday)

                        assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(HOLIDAY_ID_0))
                    }

                }
            }
        }

        @Nested
        inner class WeekdayInMonthTest {

            @Test
            fun `In unknown month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(0, 0, 2))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Calendar without weekdays is unsupported`() {
                val holiday = Holiday(HOLIDAY_ID_0, calendar = CALENDAR_ID_1, relativeDate = WeekdayInMonth(0, 0, 0))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalStateException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown weekday`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(3, 0, 0))
                val action = UpdateHoliday(holiday)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid weekday`() {
                repeat(weekdays.weekDays.size) { day ->
                    val holiday = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(day, 0, 0))
                    val action = UpdateHoliday(holiday)

                    assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(HOLIDAY_ID_0))
                }
            }

        }
    }

}