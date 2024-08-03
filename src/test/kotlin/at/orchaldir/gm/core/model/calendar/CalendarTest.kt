package at.orchaldir.gm.core.model.calendar

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CalendarTest {

    @Test
    fun `Test the number of days per year`() {
        val calendar = Calendar(CalendarId(0), months = listOf(Month("a", 10), Month("b", 5)))

        assertEquals(15, calendar.getDaysPerYear())
    }

}