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
        fun `Test year 0`() {
            assertResolve(0, 0, 0, 0)
            assertResolve(1, 0, 0, 1)
            assertResolve(2, 0, 1, 0)
            assertResolve(3, 0, 1, 1)
            assertResolve(4, 0, 1, 2)
        }

        @Test
        fun `Test year 1`() {
            assertResolve(5, 1, 0, 0)
            assertResolve(6, 1, 0, 1)
            assertResolve(7, 1, 1, 0)
            assertResolve(8, 1, 1, 1)
            assertResolve(9, 1, 1, 2)
        }

        private fun assertResolve(date: Int, year: Int, month: Int, day: Int) {
            assertEquals(CalendarDay(year, month, day), CALENDAR.resolve(Date(date)))
        }

    }

}