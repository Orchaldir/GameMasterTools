package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val CALENDAR = Calendar(CalendarId(0), months = listOf(Month("a", 2), Month("b", 3)))

class CalendarTest {

    @Test
    fun `Test the number of days per year`() {
        assertEquals(5, CALENDAR.getDaysPerYear())
    }

    @Nested
    inner class ResolveDayTest {

        @Test
        fun `Test without offset`() {
            assertTest(-10, -2)
            assertTest(-5, -1)
            assertTest(0, 1)
            assertTest(5, 2)
        }

        @Test
        fun `Test with offset`() {
            val calendar = CALENDAR.copy(offsetInDays = 12)

            assertResolve(calendar, -13, -1, 1, 2)
            assertResolve(calendar, -12, 1, 0, 0)
            assertResolve(calendar, -11, 1, 0, 1)
        }

        private fun assertTest(startDate: Int, year: Int) {
            assertResolve(CALENDAR, startDate, year, 0, 0)
            assertResolve(CALENDAR, startDate + 1, year, 0, 1)
            assertResolve(CALENDAR, startDate + 2, year, 1, 0)
            assertResolve(CALENDAR, startDate + 3, year, 1, 1)
            assertResolve(CALENDAR, startDate + 4, year, 1, 2)
        }

        private fun assertResolve(calendar: Calendar, date: Int, year: Int, month: Int, day: Int) {
            assertEquals(CalendarDay(year, month, day), calendar.resolve(Day(date)))
        }

    }

    @Nested
    inner class ResolveYearTest {

        @Test
        fun `Test without offset`() {
            assertResolve(CALENDAR, -2, -2)
            assertResolve(CALENDAR, -1, -1)
            assertResolve(CALENDAR, 0, 1)
            assertResolve(CALENDAR, 1, 2)
        }

        @Test
        fun `Test with positive offset`() {
            val calendar = CALENDAR.copy(offsetInDays = 12)
            assertResolve(calendar, -4, -2)
            assertResolve(calendar, -3, -1)
            assertResolve(calendar, -2, 1)
            assertResolve(calendar, -1, 2)
            assertResolve(calendar, 0, 3)
            assertResolve(calendar, 1, 4)
        }

        @Test
        fun `Test with negative offset`() {
            val calendar = CALENDAR.copy(offsetInDays = -5)
            assertResolve(calendar, -2, -3)
            assertResolve(calendar, -1, -2)
            assertResolve(calendar, 0, -1)
            assertResolve(calendar, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, input: Int, output: Int) {
            assertEquals(CalendarYear(output), calendar.resolve(Year(input)))
        }
    }

}