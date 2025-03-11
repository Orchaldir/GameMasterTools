package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.core.model.time.DisplayDate
import at.orchaldir.gm.core.model.time.DisplayDay
import at.orchaldir.gm.core.model.time.DisplayDecade
import at.orchaldir.gm.core.model.time.DisplayYear
import at.orchaldir.gm.core.model.time.calendar.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DisplayTest {

    private val month0 = Month("First", 2)
    private val month1 = Month("Second", 3)
    private val month2 = Month("Third", 4)
    private val calendar = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1, month2)))
    private val format0 = DateFormat()
    private val format1 = DateFormat(DateOrder.YearMonthDay, '/', true)

    @Test
    fun `Test a day in AD`() {
        val date = DisplayDay(1, 2023, 1, 2)

        assertDisplay(format0, date, "3.2.2024 AD")
        assertDisplay(format1, date, "2024/Second/3 AD")
    }

    @Test
    fun `Test a year in AD`() {
        val date = DisplayYear(1, 2023)

        assertDisplay(format0, date, "2024 AD")
        assertDisplay(format1, date, "2024 AD")
    }

    @Test
    fun `Test a decade in AD`() {
        val date = DisplayDecade(1, 202)

        assertDisplay(format0, date, "2020s AD")
        assertDisplay(format1, date, "2020s AD")
    }

    @Test
    fun `Test the first day in AD`() {
        val date = DisplayDay(1, 0, 0, 0)

        assertDisplay(format0, date, "1.1.1 AD")
        assertDisplay(format1, date, "1/First/1 AD")
    }

    @Test
    fun `Test the first year in AD`() {
        val date = DisplayYear(1, 0)

        assertDisplay(format0, date, "1 AD")
        assertDisplay(format1, date, "1 AD")
    }

    @Test
    fun `Test the first decade in AD`() {
        // not sure about this
        val date = DisplayDecade(1, 0)

        assertDisplay(format0, date, "0s AD")
        assertDisplay(format1, date, "0s AD")
    }

    @Test
    fun `Test a single digit decade in AD`() {
        val date = DisplayDecade(1, 5)

        assertDisplay(format0, date, "50s AD")
        assertDisplay(format1, date, "50s AD")
    }

    @Test
    fun `Test a day in BC`() {
        val date = DisplayDay(0, 101, 2, 3)

        assertDisplay(format0, date, "BC 4.3.102")
        assertDisplay(format1, date, "BC 102/Third/4")
    }

    @Test
    fun `Test a year in BC`() {
        val date = DisplayYear(0, 1234)

        assertDisplay(format0, date, "BC 1235")
        assertDisplay(format1, date, "BC 1235")
    }

    @Test
    fun `Test a decade in BC`() {
        val date = DisplayDecade(0, 11)

        assertDisplay(format0, date, "BC 110s")
        assertDisplay(format1, date, "BC 110s")
    }

    @Test
    fun `Test the first decade in BC`() {
        val date = DisplayDecade(0, 0)

        assertDisplay(format0, date, "BC 0s")
        assertDisplay(format1, date, "BC 0s")
    }

    private fun assertDisplay(format: DateFormat, date: DisplayDate, result: String) {
        assertEquals(result, display(calendar, format, date))
    }
}