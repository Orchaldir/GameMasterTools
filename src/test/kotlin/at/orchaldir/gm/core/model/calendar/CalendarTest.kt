package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.*
import org.junit.jupiter.api.Assertions.assertEquals
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
            assertTest(-10, 0, 1)
            assertTest(-5, 0, 0)
            assertTest(0, 1, 0)
            assertTest(5, 1, 1)
        }

        @Test
        fun `Test with offset`() {
            val calendar = createCalendar(Day(-12))

            assertResolve(calendar, -13, 0, 0, 1, 2)
            assertResolve(calendar, -12, 1, 0, 0, 0)
            assertResolve(calendar, -11, 1, 0, 0, 1)
        }

        private fun assertTest(startDate: Int, era: Int, year: Int) {
            assertResolve(CALENDAR, startDate, era, year, 0, 0)
            assertResolve(CALENDAR, startDate + 1, era, year, 0, 1)
            assertResolve(CALENDAR, startDate + 2, era, year, 1, 0)
            assertResolve(CALENDAR, startDate + 3, era, year, 1, 1)
            assertResolve(CALENDAR, startDate + 4, era, year, 1, 2)
        }

        private fun assertResolve(
            calendar: Calendar,
            date: Int,
            eraIndex: Int,
            yearIndex: Int,
            monthIndex: Int,
            dayIndex: Int,
        ) {
            val displayDay = DisplayDay(eraIndex, yearIndex, monthIndex, dayIndex)
            val day = Day(date)

            assertEquals(displayDay, calendar.resolve(day))
            assertEquals(day, calendar.resolve(displayDay))
        }

    }

    @Nested
    inner class ResolveYearTest {
        @Test
        fun `Test without offset`() {
            assertResolve(CALENDAR, -2, 0, 1)
            assertResolve(CALENDAR, -1, 0, 0)
            assertResolve(CALENDAR, 0, 1, 0)
            assertResolve(CALENDAR, 1, 1, 1)
        }

        @Test
        fun `Test with positive offset`() {
            val calendar = createCalendar(Day(-12))
            assertResolve(calendar, -4, 0, 1)
            assertResolve(calendar, -3, 0, 0)
            assertResolve(calendar, -2, 1, 0)
            assertResolve(calendar, -1, 1, 1)
            assertResolve(calendar, 0, 1, 2)
            assertResolve(calendar, 1, 1, 3)
        }

        @Test
        fun `Test with negative offset`() {
            val calendar = createCalendar(Year(1))
            assertResolve(calendar, -2, 0, 2)
            assertResolve(calendar, -1, 0, 1)
            assertResolve(calendar, 0, 0, 0)
            assertResolve(calendar, 1, 1, 0)
            assertResolve(calendar, 2, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, input: Int, eraIndex: Int, yearIndex: Int) {
            val year = Year(input)
            val displayYear = DisplayYear(eraIndex, yearIndex)

            assertEquals(displayYear, calendar.resolve(year))
            assertEquals(year, calendar.resolve(displayYear))
        }
    }

    private fun createCalendar(date: Date) = CALENDAR.copy(eras = CalendarEras("BC", true, date, "AD", false))
}