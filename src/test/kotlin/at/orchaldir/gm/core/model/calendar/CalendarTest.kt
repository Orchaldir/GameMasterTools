package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.calendar.date.CalendarDay
import at.orchaldir.gm.core.model.calendar.date.Date
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
    inner class ResolveDateTest {

        @Test
        fun `Test year -2`() {
            assertTest(-10, -2)
        }

        @Test
        fun `Test year -1`() {
            assertTest(-5, -1)
        }

        @Test
        fun `Test year 1`() {
            assertTest(0, 1)
        }

        @Test
        fun `Test year 2`() {
            assertTest(5, 2)
        }

        private fun assertTest(startDate: Int, year: Int) {
            assertResolve(startDate, year, 0, 0)
            assertResolve(startDate + 1, year, 0, 1)
            assertResolve(startDate + 2, year, 1, 0)
            assertResolve(startDate + 3, year, 1, 1)
            assertResolve(startDate + 4, year, 1, 2)
        }

        private fun assertResolve(date: Int, year: Int, month: Int, day: Int) {
            assertEquals(CalendarDay(year, month, day), CALENDAR.resolve(Date(date)))
        }

    }

}