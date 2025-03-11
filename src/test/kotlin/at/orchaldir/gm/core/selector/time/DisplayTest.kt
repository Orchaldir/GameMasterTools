package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.time.DisplayDay
import at.orchaldir.gm.core.model.time.DisplayDecade
import at.orchaldir.gm.core.model.time.DisplayYear
import at.orchaldir.gm.core.model.time.calendar.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DisplayTest {

    private val month0 = Month("First", 2)
    private val month1 = Month("Second", 3)
    private val calendar = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1)))
    private val format0 = DateFormat()
    private val format1 = DateFormat(DateOrder.YearMonthDay, '/', true)

    @Test
    fun `Test a day in AD`() {
        val date = DisplayDay(1, 2023, 1, 1)

        assertDisplay(format0, date, "2.2.2024 AD")
        assertDisplay(format1, date, "2024/Second/2 AD")
    }

    @Test
    fun `Test a year in AD`() {
        assertEquals("2024 AD", calendar.display(DisplayYear(1, 2023)))
    }

    @Test
    fun `Test a decade in AD`() {
        assertEquals("2020s AD", calendar.display(DisplayDecade(1, 202)))
    }

    @Test
    fun `Test the first day in AD`() {
        assertEquals("1.1.1 AD", calendar.display(DisplayDay(1, 0, 0, 0)))
    }

    @Test
    fun `Test the first year in AD`() {
        assertEquals("1 AD", calendar.display(DisplayYear(1, 0)))
    }

    @Test
    fun `Test the first decade in AD`() {
        // not sure about this
        assertEquals("0s AD", calendar.display(DisplayDecade(1, 0)))
    }

    @Test
    fun `Test a single digit decade in AD`() {
        assertEquals("50s AD", calendar.display(DisplayDecade(1, 5)))
    }

    @Test
    fun `Test a day in BC`() {
        assertEquals("BC 14.12.102", calendar.display(DisplayDay(0, 101, 11, 13)))
    }

    @Test
    fun `Test a year in BC`() {
        assertEquals("BC 1235", calendar.display(DisplayYear(0, 1234)))
    }

    @Test
    fun `Test a decade in BC`() {
        assertEquals("BC 110s", calendar.display(DisplayDecade(0, 11)))
    }

    @Test
    fun `Test the first decade in BC`() {
        assertEquals("BC 0s", calendar.display(DisplayDecade(0, 0)))
    }

    private fun assertDisplay(format: DateFormat, date: DisplayDay, result: String) {
        assertEquals(result, display(calendar, format, date))
    }
}