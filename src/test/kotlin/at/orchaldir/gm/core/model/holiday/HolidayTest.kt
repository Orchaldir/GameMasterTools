package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.time.Day
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
            val start = yearIndex * CALENDAR0.getDaysPerYear()
            assertIsOn(relativeDate, start + 0, false)
            assertIsOn(relativeDate, start + 1, false)
            assertIsOn(relativeDate, start + 2, false)
            assertIsOn(relativeDate, start + 3, true)
            assertIsOn(relativeDate, start + 4, false)
        }
    }

    @Nested
    inner class IsOnWeekdayTest {

        private val relativeDate = WeekdayInMonth(0, 1, 1)

        @Test
        fun `Year 0`() {
            assertIsOn(relativeDate, 0, false)
            assertIsOn(relativeDate, 1, false)
            assertIsOn(relativeDate, 2, false)
            assertIsOn(relativeDate, 3, false)
            assertIsOn(relativeDate, 4, true)
        }
    }

    private fun assertIsOn(
        relativeDate: RelativeDate,
        dayIndex: Int,
        result: Boolean,
    ) {
        val displayDay = CALENDAR0.resolve(Day(dayIndex))
        assertEquals(result, relativeDate.isOn(CALENDAR0, displayDay))
    }

}