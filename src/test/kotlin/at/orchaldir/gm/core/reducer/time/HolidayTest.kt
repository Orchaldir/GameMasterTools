package at.orchaldir.gm.core.reducer.time

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.holiday.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HolidayTest {

    private val weekdays = Weekdays(listOf(WeekDay(DAY_NAME1), WeekDay(DAY_NAME1)))
    val monthsList = listOf(MonthDefinition(NAME0, 2), MonthDefinition(NAME1, 3))
    private val months = ComplexMonths(monthsList)
    private val calendar0 = Calendar(CALENDAR_ID_0, days = weekdays, months = months)
    private val calendar1 = Calendar(CALENDAR_ID_1, months = months)
    private val state = State(
        listOf(
            Storage(listOf(calendar0, calendar1)),
            Storage(Holiday(HOLIDAY_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Holiday(UNKNOWN_HOLIDAY_ID))

            assertIllegalArgument("Requires unknown Holiday 99!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot update with unknown calendar`() {
            val holiday = Holiday(HOLIDAY_ID_0, calendar = UNKNOWN_CALENDAR_ID)
            val action = UpdateAction(holiday)

            assertIllegalArgument("Requires unknown Calendar 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown catastrophe`() {
            val holiday = Holiday(HOLIDAY_ID_0, purpose = HolidayOfCatastrophe(UNKNOWN_CATASTROPHE_ID))
            val action = UpdateAction(holiday)

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown god`() {
            val holiday = Holiday(HOLIDAY_ID_0, purpose = HolidayOfGod(UNKNOWN_GOD_ID))
            val action = UpdateAction(holiday)

            assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown treaty`() {
            val holiday = Holiday(HOLIDAY_ID_0, purpose = HolidayOfTreaty(UNKNOWN_TREATY_ID))
            val action = UpdateAction(holiday)

            assertIllegalArgument("Requires unknown Treaty 99!") { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class DayInYearTest {

            @Test
            fun `In unknown month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(0, 2))
                val action = UpdateAction(holiday)

                assertIllegalArgument("Holiday is in an unknown month!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Outside first month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(2, 0))
                val action = UpdateAction(holiday)

                assertIllegalArgument("Holiday is outside the month A!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Outside second month`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(3, 1))
                val action = UpdateAction(holiday)

                assertIllegalArgument("Holiday is outside the month B!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid fixed days`() {
                months.months.withIndex().forEach { month ->
                    repeat(month.value.days) { day ->
                        val holiday = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(day, month.index))
                        val action = UpdateAction(holiday)

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
                val action = UpdateAction(holiday)

                assertIllegalArgument("Holiday is in an unknown month!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Calendar without weekdays is unsupported`() {
                val holiday = Holiday(HOLIDAY_ID_0, calendar = CALENDAR_ID_1, relativeDate = WeekdayInMonth(0, 0, 0))
                val action = UpdateAction(holiday)

                assertFailsWith<IllegalStateException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown weekday`() {
                val holiday = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(3, 0, 0))
                val action = UpdateAction(holiday)

                assertIllegalArgument("Holiday is on an unknown weekday!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid weekday`() {
                repeat(weekdays.weekDays.size) { day ->
                    val holiday = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(day, 0, 0))
                    val action = UpdateAction(holiday)

                    assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(HOLIDAY_ID_0))
                }
            }

        }
    }

}