package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.time.DisplayDay
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val CALENDAR_ID0 = CalendarId(0)
private val WEEKDAYS = Weekdays(listOf(WeekDay("d0"), WeekDay("d1")))
private val MONTHS = listOf(MonthDefinition("M0", 2), MonthDefinition("M1", 3))
private val CALENDAR0 = Calendar(CALENDAR_ID0, "C0", WEEKDAYS, months = MONTHS)

class HolidayTest {

    @Nested
    inner class IsOnFixedDayTest {

        private val relativeDate = FixedDayInYear(1, 1)

        @Test
        fun `Year 0`() {
            assertYear(relativeDate, 0)
        }

        @Test
        fun `Year 1`() {
            assertYear(relativeDate, 1)
        }

        private fun assertYear(relativeDate: FixedDayInYear, yearIndex: Int) {
            assertIsOn(relativeDate, yearIndex, 0, 0, false)
            assertIsOn(relativeDate, yearIndex, 0, 1, false)
            assertIsOn(relativeDate, yearIndex, 1, 0, false)
            assertIsOn(relativeDate, yearIndex, 1, 1, true)
            assertIsOn(relativeDate, yearIndex, 1, 2, false)
        }
    }

    @Nested
    inner class IsOnWeekdayTest {

        private val relativeDate = WeekdayInMonth(0, 1, 1)

        @Test
        fun `Year 0`() {
            assertIsOn(relativeDate, 0, 0, 0, false)
            assertIsOn(relativeDate, 0, 0, 1, false)
            assertIsOn(relativeDate, 0, 1, 0, false)
            assertIsOn(relativeDate, 0, 1, 1, false)
            assertIsOn(relativeDate, 0, 1, 2, true)
        }
    }

    private fun assertIsOn(
        relativeDate: RelativeDate,
        yearIndex: Int,
        monthIndex: Int,
        dayIndex: Int,
        result: Boolean,
    ) {
        assertEquals(result, relativeDate.isOn(CALENDAR0, DisplayDay(1, yearIndex, monthIndex, dayIndex)))
    }

}