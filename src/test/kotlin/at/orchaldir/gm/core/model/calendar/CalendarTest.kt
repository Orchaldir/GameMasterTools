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
            assertEquals(CalendarDay(0, 0, 0), CALENDAR.resolve(Date(0)))
        }

    }

}