package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResolveTest {

    private val month0 = MonthDefinition("a", 2)
    private val month1 = MonthDefinition("b", 3)
    private val days = Weekdays(listOf(WeekDay("d0"), WeekDay("d1")))
    private val calendar0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1)))
    private val calendar1 = calendar0.copy(days = days)
    private val years = calendar0.getDaysPerYear()

    @Nested
    inner class ResolveDayTest {

        @Test
        fun `Test without offset`() {
            assertYears(calendar0)
        }

        @Test
        fun `Ignore negative offset`() {
            assertYears(createCalendar(Day(-12)))
        }

        @Test
        fun `Ignore positive offset`() {
            assertYears(createCalendar(Day(12)))
        }

        private fun assertYears(calendar: Calendar) {
            assertYear(calendar, -10, 0, 1)
            assertYear(calendar, -5, 0, 0)
            assertYear(calendar, 0, 1, 0)
            assertYear(calendar, 5, 1, 1)
        }

        private fun assertYear(calendar: Calendar, startDate: Int, era: Int, year: Int) {
            assertResolve(calendar, startDate, era, year, 0, 0)
            assertResolve(calendar, startDate + 1, era, year, 0, 1)
            assertResolve(calendar, startDate + 2, era, year, 1, 0)
            assertResolve(calendar, startDate + 3, era, year, 1, 1)
            assertResolve(calendar, startDate + 4, era, year, 1, 2)
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
    inner class ResolveWeekTest {
        @Test
        fun `Test without offset`() {
            test(calendar1)
        }

        @Test
        fun `Ignore negative offset`() {
            test(createCalendar(calendar1, Day(-12)))
        }

        @Test
        fun `Ignore positive offset`() {
            test(createCalendar(calendar1, Day(12)))
        }

        private fun test(calendar: Calendar) {
            assertResolve(calendar, -2, 0, 0, 0)
            assertResolve(calendar, -1, 0, 0, 1)
            assertResolve(calendar, 0, 1, 0, 0) // 1 AD
            assertResolve(calendar, 1, 1, 0, 1)
            assertResolve(calendar, 2, 1, 0, 2)
            assertResolve(calendar, 3, 1, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputWeek: Int, eraIndex: Int, yearIndex: Int, weekIndex: Int) {
            val week = Week(inputWeek)
            val displayWeek = DisplayWeek(eraIndex, yearIndex, weekIndex)

            assertEquals(displayWeek, calendar.resolveWeek(week))
            assertEquals(week, calendar.resolveWeek(displayWeek))
        }
    }

    @Nested
    inner class ResolveMonthTest {
        @Test
        fun `Test without offset`() {
            test(calendar0)
        }

        @Test
        fun `Ignore negative offset`() {
            test(createCalendar(Day(-12)))
        }

        @Test
        fun `Ignore positive offset`() {
            test(createCalendar(Day(12)))
        }

        private fun test(calendar: Calendar) {
            assertResolve(calendar, -3, 0, 1, 1)
            assertResolve(calendar, -2, 0, 0, 0)
            assertResolve(calendar, -1, 0, 0, 1)
            assertResolve(calendar, 0, 1, 0, 0) // 1 AD
            assertResolve(calendar, 1, 1, 0, 1)
        }

        private fun assertResolve(calendar: Calendar, inputMonth: Int, eraIndex: Int, yearIndex: Int, monthIndex: Int) {
            val month = Month(inputMonth)
            val displayMonth = DisplayMonth(eraIndex, yearIndex, monthIndex)

            assertEquals(displayMonth, calendar.resolveMonth(month))
            assertEquals(month, calendar.resolveMonth(displayMonth))
        }
    }

    @Nested
    inner class ResolveYearTest {
        @Test
        fun `Test without offset`() {
            test(calendar0)
        }

        @Test
        fun `Ignore negative offset`() {
            test(createCalendar(Day(-years * 2)))
        }

        @Test
        fun `Ignore positive offset`() {
            test(createCalendar(Day(years * 2)))
        }

        private fun test(calendar: Calendar) {
            assertResolve(calendar, -2, 0, 1)
            assertResolve(calendar, -1, 0, 0)
            assertResolve(calendar, 0, 1, 0) // 1 AD
            assertResolve(calendar, 1, 1, 1)
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
            test(calendar0)
        }

        @Test
        fun `Ignore negative offset`() {
            test(createCalendar(Day(-years * 20)))
        }

        @Test
        fun `Ignore positive offset`() {
            test(createCalendar(Day(years * 20)))
        }

        private fun test(calendar: Calendar) {
            assertResolve(calendar, -2, 0, 1)
            assertResolve(calendar, -1, 0, 0)
            assertResolve(calendar, 0, 1, 0) // 1 AD
            assertResolve(calendar, 1, 1, 1)
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
            test(calendar0)
        }

        @Test
        fun `Ignore negative offset`() {
            test(createCalendar(Day(-years * 200)))
        }

        @Test
        fun `Ignore positive offset`() {
            test(createCalendar(Day(years * 200)))
        }

        private fun test(calendar: Calendar) {
            assertResolve(calendar, -2, 0, 1)
            assertResolve(calendar, -1, 0, 0)
            assertResolve(calendar, 0, 1, 0) // 1 AD
            assertResolve(calendar, 1, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputCentury: Int, eraIndex: Int, centuryIndex: Int) {
            val century = Century(inputCentury)
            val displayCentury = DisplayCentury(eraIndex, centuryIndex)

            assertEquals(displayCentury, calendar.resolveCentury(century))
            assertEquals(century, calendar.resolveCentury(displayCentury))
        }
    }

    private fun createCalendar(date: Day) = createCalendar(calendar0, date)

    private fun createCalendar(calendar: Calendar, date: Day) = calendar
        .copy(eras = CalendarEras("BC", true, date, "AD", false))
}