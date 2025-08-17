package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.NAME0
import at.orchaldir.gm.NAME1
import at.orchaldir.gm.NAME2
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DisplayTest {

    private val month0 = MonthDefinition(NAME0, 2)
    private val month1 = MonthDefinition(NAME1, 3)
    private val month2 = MonthDefinition(NAME2, 4)
    private val calendar = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1, month2)))
    private val format0 = DateFormat()
    private val format1 = DateFormat(DateOrder.YearMonthDay, '/', true)
    private val dayAd = DisplayDay(1, 2023, 1, 2)
    private val dayBc = DisplayDay(0, 101, 2, 3)

    @Nested
    inner class AdTest {

        @Test
        fun `Test a day in AD`() {
            assertDisplay(format0, dayAd, "3.2.2024 AD")
            assertDisplay(format1, dayAd, "2024/B/3 AD")
        }

        @Test
        fun `Test a week in AD`() {
            val date = DisplayWeek(1, 2023, 1)
            val result = "2.Week of 2024 AD"

            assertDisplay(format0, date, result)
            assertDisplay(format1, date, result)
        }

        @Test
        fun `Test a month in AD`() {
            val date = DisplayMonth(1, 2023, 1)

            assertDisplay(format0, date, "2.2024 AD")
            assertDisplay(format1, date, "2024/B AD")
        }

        @Test
        fun `Test a year in AD`() {
            val date = DisplayYear(1, 2023)

            assertDisplay(format0, date, "2024 AD")
            assertDisplay(format1, date, "2024 AD")
        }

        @Test
        fun `Test an approximate year in AD`() {
            val date = DisplayApproximateYear(1, 2023)

            assertDisplay(format0, date, "~2024 AD")
            assertDisplay(format1, date, "~2024 AD")
        }

        @Test
        fun `Test a decade in AD`() {
            val date = DisplayDecade(1, 202)

            assertDisplay(format0, date, "2020s AD")
            assertDisplay(format1, date, "2020s AD")
        }

        @Test
        fun `Test a century in AD`() {
            val date = DisplayCentury(1, 20)

            assertDisplay(format0, date, "21.century AD")
            assertDisplay(format1, date, "21.century AD")
        }

        @Test
        fun `Test a millennium in AD`() {
            val date = DisplayMillennium(1, 2)

            assertDisplay(format0, date, "3.millennium AD")
            assertDisplay(format1, date, "3.millennium AD")
        }

        @Test
        fun `Test the first day in AD`() {
            val date = DisplayDay(1, 0, 0, 0)

            assertDisplay(format0, date, "1.1.1 AD")
            assertDisplay(format1, date, "1/A/1 AD")
        }

        @Test
        fun `Test the first month in AD`() {
            val date = DisplayMonth(1, 0, 0)

            assertDisplay(format0, date, "1.1 AD")
            assertDisplay(format1, date, "1/A AD")
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
        fun `Test the first century in AD`() {
            val date = DisplayCentury(1, 0)

            assertDisplay(format0, date, "1.century AD")
            assertDisplay(format1, date, "1.century AD")
        }

        @Test
        fun `Test the first millennium in AD`() {
            val date = DisplayMillennium(1, 0)

            assertDisplay(format0, date, "1.millennium AD")
            assertDisplay(format1, date, "1.millennium AD")
        }
    }

    @Nested
    inner class BcTest {

        @Test
        fun `Test a day in BC`() {
            assertDisplay(format0, dayBc, "BC 4.3.102")
            assertDisplay(format1, dayBc, "BC 102/C/4")
        }

        @Test
        fun `Test a week in BC`() {
            val date = DisplayWeek(0, 101, 0)
            val result = "1.Week of BC 102"

            assertDisplay(format0, date, result)
            assertDisplay(format1, date, result)
        }

        @Test
        fun `Test a month in BC`() {
            val date = DisplayMonth(0, 101, 2)

            assertDisplay(format0, date, "BC 3.102")
            assertDisplay(format1, date, "BC 102/C")
        }

        @Test
        fun `Test a year in BC`() {
            val date = DisplayYear(0, 1234)

            assertDisplay(format0, date, "BC 1235")
            assertDisplay(format1, date, "BC 1235")
        }

        @Test
        fun `Test an approximate year in AD`() {
            val date = DisplayApproximateYear(0, 1234)

            assertDisplay(format0, date, "BC ~1235")
            assertDisplay(format1, date, "BC ~1235")
        }

        @Test
        fun `Test a decade in BC`() {
            val date = DisplayDecade(0, 11)

            assertDisplay(format0, date, "BC 110s")
            assertDisplay(format1, date, "BC 110s")
        }

        @Test
        fun `Test a century in AD`() {
            val date = DisplayCentury(0, 2)

            assertDisplay(format0, date, "BC 3.century")
            assertDisplay(format1, date, "BC 3.century")
        }

        @Test
        fun `Test a millennium in AD`() {
            val date = DisplayMillennium(0, 2)

            assertDisplay(format0, date, "BC 3.millennium")
            assertDisplay(format1, date, "BC 3.millennium")
        }

        @Test
        fun `Test the first month in BC`() {
            val date = DisplayMonth(0, 0, 0)

            assertDisplay(format0, date, "BC 1.1")
            assertDisplay(format1, date, "BC 1/A")
        }

        @Test
        fun `Test the first decade in BC`() {
            val date = DisplayDecade(0, 0)

            assertDisplay(format0, date, "BC 0s")
            assertDisplay(format1, date, "BC 0s")
        }

        @Test
        fun `Test the first century in AD`() {
            val date = DisplayCentury(0, 0)

            assertDisplay(format0, date, "BC 1.century")
            assertDisplay(format1, date, "BC 1.century")
        }

        @Test
        fun `Test the first millennium in AD`() {
            val date = DisplayMillennium(0, 0)

            assertDisplay(format0, date, "BC 1.millennium")
            assertDisplay(format1, date, "BC 1.millennium")
        }
    }

    @Test
    fun `Test a day range`() {
        val date = DisplayDayRange(dayBc, dayAd)

        assertDisplay(format0, date, "BC 4.3.102 to 3.2.2024 AD")
        assertDisplay(format1, date, "BC 102/C/4 to 2024/B/3 AD")
    }

    private fun assertDisplay(format: DateFormat, date: DisplayDate, result: String) {
        assertEquals(result, display(calendar, format, date))
    }
}