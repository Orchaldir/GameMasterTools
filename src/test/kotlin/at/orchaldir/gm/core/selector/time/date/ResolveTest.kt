package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResolveTest {

    private val month0 = MonthDefinition("a", 2)
    private val month1 = MonthDefinition("b", 3)
    private val calendar0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1)))

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
            assertResolve(calendar0, startDate, era, year, 0, 0)
            assertResolve(calendar0, startDate + 1, era, year, 0, 1)
            assertResolve(calendar0, startDate + 2, era, year, 1, 0)
            assertResolve(calendar0, startDate + 3, era, year, 1, 1)
            assertResolve(calendar0, startDate + 4, era, year, 1, 2)
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

            assertEquals(displayDay, calendar.resolveDay(day))
            assertEquals(day, calendar.resolveDay(displayDay))
        }

    }

    @Nested
    inner class ResolveMonthTest {
        @Test
        fun `Test without offset`() {
            assertResolve(calendar0, -3, 0, 1, 1)
            assertResolve(calendar0, -2, 0, 0, 0)
            assertResolve(calendar0, -1, 0, 0, 1)
            assertResolve(calendar0, 0, 1, 0, 0) // 1 AD
            assertResolve(calendar0, 1, 1, 0, 1)
        }

        private fun assertResolve(calendar: Calendar, inputMonth: Int, eraIndex: Int, yearIndex: Int, monthIndex: Int) {
            val month = Month(inputMonth)
            val displayMonth = DisplayMonth(eraIndex, yearIndex, monthIndex)

            assertEquals(displayMonth, calendar.resolveMonth(month))
            //assertEquals(month, calendar.resolveYear(displayMonth))
        }
    }

    @Nested
    inner class ResolveYearTest {
        @Test
        fun `Test without offset`() {
            assertResolve(calendar0, -2, 0, 1)
            assertResolve(calendar0, -1, 0, 0)
            assertResolve(calendar0, 0, 1, 0) // 1 AD
            assertResolve(calendar0, 1, 1, 1)
        }

        @Test
        fun `Test with negative offset`() {
            val calendar = createCalendar(Day(-12))

            assertResolve(calendar, -4, 0, 1)
            assertResolve(calendar, -3, 0, 0)
            assertResolve(calendar, -2, 1, 0)
            assertResolve(calendar, -1, 1, 1)
            assertResolve(calendar, 0, 1, 2) // 1 AD
            assertResolve(calendar, 1, 1, 3)
        }

        @Test
        fun `Test with positive offset`() {
            val calendar = createCalendar(Day(5))

            assertResolve(calendar, -2, 0, 2)
            assertResolve(calendar, -1, 0, 1)
            assertResolve(calendar, 0, 0, 0)
            assertResolve(calendar, 1, 1, 0) // 1 AD
            assertResolve(calendar, 2, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputYear: Int, eraIndex: Int, yearIndex: Int) {
            val year = Year(inputYear)
            val displayYear = DisplayYear(eraIndex, yearIndex)

            assertEquals(displayYear, calendar.resolveYear(year))
            assertEquals(year, calendar.resolveYear(displayYear))
        }
    }

    @Nested
    inner class ResolveDecadeTest {
        @Test
        fun `Test without offset`() {
            assertResolve(calendar0, -2, 0, 1)
            assertResolve(calendar0, -1, 0, 0)
            assertResolve(calendar0, 0, 1, 0) // 1 AD
            assertResolve(calendar0, 1, 1, 1)
        }

        @Test
        fun `Test with positive offset`() {
            val calendar = createCalendar(Day(50))

            assertResolve(calendar, -2, 0, 2)
            assertResolve(calendar, -1, 0, 1)
            assertResolve(calendar, 0, 0, 0)
            assertResolve(calendar, 1, 1, 0) // 1 AD
            assertResolve(calendar, 2, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputDecade: Int, eraIndex: Int, decadeIndex: Int) {
            val decade = Decade(inputDecade)
            val displayYDecade = DisplayDecade(eraIndex, decadeIndex)

            assertEquals(displayYDecade, calendar.resolveDecade(decade))
            assertEquals(decade, calendar.resolveDecade(displayYDecade))
        }
    }

    @Nested
    inner class ResolveCenturyTest {
        @Test
        fun `Test without offset`() {
            assertResolve(calendar0, -2, 0, 1)
            assertResolve(calendar0, -1, 0, 0)
            assertResolve(calendar0, 0, 1, 0) // 1 AD
            assertResolve(calendar0, 1, 1, 1)
        }

        @Test
        fun `Test with positive offset`() {
            val calendar = createCalendar(Day(500))

            assertResolve(calendar, -2, 0, 2)
            assertResolve(calendar, -1, 0, 1)
            assertResolve(calendar, 0, 0, 0)
            assertResolve(calendar, 1, 1, 0) // 1 AD
            assertResolve(calendar, 2, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputCentury: Int, eraIndex: Int, centuryIndex: Int) {
            val century = Century(inputCentury)
            val displayCentury = DisplayCentury(eraIndex, centuryIndex)

            assertEquals(displayCentury, calendar.resolveCentury(century))
            assertEquals(century, calendar.resolveCentury(displayCentury))
        }
    }

    private fun createCalendar(date: Day) = calendar0
        .copy(eras = CalendarEras("BC", true, date, "AD", false))
}