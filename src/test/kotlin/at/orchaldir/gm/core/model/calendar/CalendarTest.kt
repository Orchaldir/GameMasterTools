package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val MONTH0 = MonthDefinition("a", 2)
private val MONTH1 = MonthDefinition("b", 3)
private val CALENDAR0 = Calendar(CalendarId(0), months = listOf(MONTH0, MONTH1))
private val CALENDAR1 = Calendar(CalendarId(1), days = Weekdays(listOf(WeekDay("D0"), WeekDay("D1"), WeekDay("D2"))))

class CalendarTest {

    @Test
    fun `Get the start of a year`() {
        assertEquals(Day(5), CALENDAR0.getStartOfYear(Year(1)))
        assertEquals(Day(10), CALENDAR0.getStartOfYear(Year(2)))
    }

    @Test
    fun `Get the end of a year`() {
        assertEquals(Day(9), CALENDAR0.getEndOfYear(Year(1)))
        assertEquals(Day(14), CALENDAR0.getEndOfYear(Year(2)))
    }

    @Nested
    inner class IsAfterOrEqualTest {
        @Test
        fun `Test a greater than b`() {
            assertTrue(CALENDAR0.isAfterOrEqual(Year(2), Year(1)))
        }

        @Test
        fun `Test a equal to b`() {
            assertTrue(CALENDAR0.isAfterOrEqual(Year(1), Year(1)))
        }

        @Test
        fun `Test a less than b`() {
            assertFalse(CALENDAR0.isAfterOrEqual(Year(1), Year(2)))
        }
    }

    @Nested
    inner class GetDurationInYearsTest {

        @Test
        fun `From a year to the same year`() {
            assertWholeYear(Year(1), 5, 0)
        }

        @Test
        fun `From a day to the same year`() {
            assertWholeYear(Day(5), 5, 0)
        }

        @Test
        fun `From a year to the next year`() {
            assertWholeYear(Year(1), 10, 1)
        }

        @Test
        fun `From a day to the next year`() {
            assertWholeYear(Day(5), 10, 1)
        }

        private fun assertWholeYear(from: Date, toStart: Int, result: Int) {
            assertGetDuration(from, toStart, result)
            assertGetDuration(from, toStart + 1, result)
            assertGetDuration(from, toStart + 2, result)
            assertGetDuration(from, toStart + 3, result)
            assertGetDuration(from, toStart + 4, result)
        }

        private fun assertGetDuration(from: Date, to: Int, result: Int) {
            assertEquals(result, CALENDAR0.getDurationInYears(from, Day(to)))
        }
    }

    @Nested
    inner class GetMonthTest {

        @Test
        fun `Get months of negative days`() {
            assertEquals(MONTH0, CALENDAR0.getMonth(Day(-5)))
            assertEquals(MONTH0, CALENDAR0.getMonth(Day(-4)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(-3)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(-2)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(-1)))
        }

        @Test
        fun `Get months of the first year`() {
            assertEquals(MONTH0, CALENDAR0.getMonth(Day(0)))
            assertEquals(MONTH0, CALENDAR0.getMonth(Day(1)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(2)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(3)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(4)))
        }

        @Test
        fun `Get months of the second year`() {
            assertEquals(MONTH0, CALENDAR0.getMonth(Day(5)))
            assertEquals(MONTH0, CALENDAR0.getMonth(Day(6)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(7)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(8)))
            assertEquals(MONTH1, CALENDAR0.getMonth(Day(9)))
        }

        @Test
        fun `Test with offset`() {
            val calendar = CALENDAR0.copy(eras = CalendarEras("BC", true, Day(1), "AD", false))

            assertEquals(MONTH1, calendar.getMonth(Day(0)))
            assertEquals(MONTH0, calendar.getMonth(Day(1)))
        }
    }

    @Test
    fun `Get last month index`() {
        assertEquals(1, CALENDAR0.getLastMonthIndex())
    }

    @Test
    fun `Get start of month`() {
        assertEquals(Day(2), CALENDAR0.getStartOfMonth(Day(2)))
        assertEquals(Day(2), CALENDAR0.getStartOfMonth(Day(3)))
        assertEquals(Day(2), CALENDAR0.getStartOfMonth(Day(4)))
    }

    @Test
    fun `Get end of month`() {
        assertEquals(Day(1), CALENDAR0.getEndOfMonth(Day(0)))
        assertEquals(Day(1), CALENDAR0.getEndOfMonth(Day(1)))
    }

    @Nested
    inner class GetStartOfNextMonthTest {
        @Test
        fun `Get start of next month in first era`() {
            assertEquals(Day(-3), CALENDAR0.getStartOfNextMonth(Day(-5)))
            assertEquals(Day(-3), CALENDAR0.getStartOfNextMonth(Day(-4)))
        }

        @Test
        fun `Get start of next month across era`() {
            assertEquals(Day(0), CALENDAR0.getStartOfNextMonth(Day(-3)))
            assertEquals(Day(0), CALENDAR0.getStartOfNextMonth(Day(-2)))
            assertEquals(Day(0), CALENDAR0.getStartOfNextMonth(Day(-1)))
        }

        @Test
        fun `Get start of next month`() {
            assertEquals(Day(2), CALENDAR0.getStartOfNextMonth(Day(0)))
            assertEquals(Day(2), CALENDAR0.getStartOfNextMonth(Day(1)))
        }

        @Test
        fun `In the next year`() {
            assertEquals(Day(5), CALENDAR0.getStartOfNextMonth(Day(2)))
            assertEquals(Day(5), CALENDAR0.getStartOfNextMonth(Day(3)))
            assertEquals(Day(5), CALENDAR0.getStartOfNextMonth(Day(4)))
        }
    }

    @Nested
    inner class GetStartOfPreviousMonthTest {
        @Test
        fun `Get start of previous month in first era`() {
            assertEquals(Day(-5), CALENDAR0.getStartOfPreviousMonth(Day(-3)))
            assertEquals(Day(-5), CALENDAR0.getStartOfPreviousMonth(Day(-2)))
            assertEquals(Day(-5), CALENDAR0.getStartOfPreviousMonth(Day(-1)))
        }

        @Test
        fun `Get start of previous month`() {
            assertEquals(Day(-3), CALENDAR0.getStartOfPreviousMonth(Day(0)))
            assertEquals(Day(-3), CALENDAR0.getStartOfPreviousMonth(Day(1)))
        }

        @Test
        fun `In the next year`() {
            assertEquals(Day(0), CALENDAR0.getStartOfPreviousMonth(Day(2)))
            assertEquals(Day(0), CALENDAR0.getStartOfPreviousMonth(Day(3)))
            assertEquals(Day(0), CALENDAR0.getStartOfPreviousMonth(Day(4)))
        }
    }

    @Nested
    inner class GetWeekDayTest {

        @Test
        fun `Day of the month returns 0`() {
            assertNull(CALENDAR0.getWeekDay(Day(0)))
            assertNull(CALENDAR0.getWeekDay(Day(1)))
        }

        @Test
        fun `First week`() {
            assertEquals(0, CALENDAR1.getWeekDay(Day(0)))
            assertEquals(1, CALENDAR1.getWeekDay(Day(1)))
            assertEquals(2, CALENDAR1.getWeekDay(Day(2)))
        }

        @Test
        fun `Second week`() {
            assertEquals(0, CALENDAR1.getWeekDay(Day(3)))
            assertEquals(1, CALENDAR1.getWeekDay(Day(4)))
            assertEquals(2, CALENDAR1.getWeekDay(Day(5)))
        }

        @Test
        fun `Negative days`() {
            assertEquals(2, CALENDAR1.getWeekDay(Day(-4)))
            assertEquals(0, CALENDAR1.getWeekDay(Day(-3)))
            assertEquals(1, CALENDAR1.getWeekDay(Day(-2)))
            assertEquals(2, CALENDAR1.getWeekDay(Day(-1)))
        }
    }

    @Test
    fun `Test the number of days per year`() {
        assertEquals(5, CALENDAR0.getDaysPerYear())
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
            assertResolve(CALENDAR0, startDate, era, year, 0, 0)
            assertResolve(CALENDAR0, startDate + 1, era, year, 0, 1)
            assertResolve(CALENDAR0, startDate + 2, era, year, 1, 0)
            assertResolve(CALENDAR0, startDate + 3, era, year, 1, 1)
            assertResolve(CALENDAR0, startDate + 4, era, year, 1, 2)
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
            assertResolve(CALENDAR0, -2, 0, 1)
            assertResolve(CALENDAR0, -1, 0, 0)
            assertResolve(CALENDAR0, 0, 1, 0)
            assertResolve(CALENDAR0, 1, 1, 1)
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

    private fun createCalendar(date: Date) = CALENDAR0.copy(eras = CalendarEras("BC", true, date, "AD", false))
}