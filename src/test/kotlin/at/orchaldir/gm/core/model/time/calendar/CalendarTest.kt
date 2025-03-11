package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.date.resolveYear
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalendarTest {

    private val MONTH0 = Month("a", 2)
    private val MONTH1 = Month("b", 3)
    private val CALENDAR0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(MONTH0, MONTH1)))
    private val CALENDAR1 =
        Calendar(CalendarId(1), days = Weekdays(listOf(WeekDay("D0"), WeekDay("D1"), WeekDay("D2"))))
    private val year0 = Year(0)
    private val year1 = Year(1)
    private val year2 = Year(2)

    @Nested
    inner class DataTest {

        @Test
        fun `Test the number of days per year`() {
            assertEquals(5, CALENDAR0.getDaysPerYear())
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DayTest {

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

    }

    @Nested
    inner class MonthTest {

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
    }

    @Nested
    inner class YearTest {

        @Nested
        inner class GetYearTest {

            @Test
            fun `Get the year of a day`() {
                assertGetYear(Day(-10), -2)
                assertGetYear(Day(-5), -1)
                assertGetYear(Day(0), 0)
                assertGetYear(Day(5), 1)
                assertGetYear(Day(10), 2)
            }

            @Test
            fun `Get the year of a year`() {
                assertGetYear(year2, 2)
            }

            @Test
            fun `Get the year of a decade`() {
                assertGetYear(Decade(-2), -19)
                assertGetYear(Decade(-1), -9)
                assertGetYear(Decade(0), 0)
                assertGetYear(Decade(1), 9)
                assertGetYear(Decade(2), 19)
            }

            private fun assertGetYear(input: Date, year: Int) {
                val year = Year(year)
                val display = CALENDAR0.resolveYear(year)

                assertEquals(year, CALENDAR0.getYear(input))
                assertEquals(display, CALENDAR0.getDisplayYear(input))
            }
        }
    }

    @Nested
    inner class DecadeTest {

        @Nested
        inner class GetDecadeTest {
            private val decade = Decade(0)
            private val display = DisplayDecade(1, 0)

            @Test
            fun `Get the decade of a decade`() {
                assertGetDecade(decade)
            }

            @Test
            fun `Get the decade of a year`() {
                assertGetDecade(Year(5))
            }

            @Test
            fun `Get the decade of a day`() {
                assertGetDecade(Day(25))
            }

            private fun assertGetDecade(date: Date) {
                assertEquals(decade, CALENDAR0.getDecade(date))
                assertEquals(display, CALENDAR0.getDisplayDecade(date))
            }

        }
    }

    @Nested
    inner class CompareTest {

        @Nested
        inner class CompareToOptionalTest {
            @Test
            fun `Test a greater than b`() {
                assertEquals(1, CALENDAR0.compareToOptional(year2, year1))
            }

            @Test
            fun `Test a equal to b`() {
                assertEquals(0, CALENDAR0.compareToOptional(year1, year1))
            }

            @Test
            fun `Test a less than b`() {
                assertEquals(-1, CALENDAR0.compareToOptional(year1, year2))
            }

            @Test
            fun `Given a is null, then return 0`() {
                assertEquals(-1, CALENDAR0.compareToOptional(null, year1))
            }

            @Test
            fun `Given b is null, then return 0`() {
                assertEquals(1, CALENDAR0.compareToOptional(year1, null))
            }

            @Test
            fun `Given both are null, then return 0`() {
                assertEquals(0, CALENDAR0.compareToOptional(null, null))
            }
        }

        @Nested
        inner class IsAfterTest {
            @Test
            fun `Test a greater than b`() {
                assertTrue(CALENDAR0.isAfter(year2, year1))
            }

            @Test
            fun `Test a equal to b`() {
                assertFalse(CALENDAR0.isAfter(year1, year1))
            }

            @Test
            fun `Test a less than b`() {
                assertFalse(CALENDAR0.isAfter(year1, year2))
            }
        }

        @Nested
        inner class IsAfterOrEqualTest {
            @Test
            fun `Test a greater than b`() {
                assertTrue(CALENDAR0.isAfterOrEqual(year2, year1))
            }

            @Test
            fun `Test a equal to b`() {
                assertTrue(CALENDAR0.isAfterOrEqual(year1, year1))
            }

            @Test
            fun `Test a less than b`() {
                assertFalse(CALENDAR0.isAfterOrEqual(year1, year2))
            }
        }

        @Nested
        inner class IsAfterOrEqualOptionalTest {
            @Test
            fun `Test a greater than b`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(year2, year1))
            }

            @Test
            fun `Test a equal to b`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(year1, year1))
            }

            @Test
            fun `Test a less than b`() {
                assertFalse(CALENDAR0.isAfterOrEqualOptional(year1, year2))
            }

            @Test
            fun `Test a is null`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(null, year1))
            }

            @Test
            fun `Test b is null`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(year1, null))
            }
        }

        @Nested
        inner class MaxTest {
            @Test
            fun `Test a greater than b`() {
                assertEquals(year2, CALENDAR0.max(year2, year1))
            }

            @Test
            fun `Test a equal to b`() {
                assertEquals(year1, CALENDAR0.max(year1, year1))
            }

            @Test
            fun `Test a less than b`() {
                assertEquals(year2, CALENDAR0.max(year1, year2))
            }

            @Test
            fun `Test b is null`() {
                assertEquals(year1, CALENDAR0.max(year1, null))
            }
        }

        @Nested
        inner class MaxOptionalTest {
            @Test
            fun `Test a greater than b`() {
                assertEquals(year2, CALENDAR0.maxOptional(year2, year1))
            }

            @Test
            fun `Test a equal to b`() {
                assertEquals(year1, CALENDAR0.maxOptional(year1, year1))
            }

            @Test
            fun `Test a less than b`() {
                assertEquals(year2, CALENDAR0.maxOptional(year1, year2))
            }

            @Test
            fun `Test a is null`() {
                assertEquals(year1, CALENDAR0.maxOptional(null, year1))
            }

            @Test
            fun `Test b is null`() {
                assertEquals(year1, CALENDAR0.maxOptional(year1, null))
            }
        }
    }

    @Nested
    inner class DurationTest {

        @Nested
        inner class GetDurationInYearsTest {

            @Test
            fun `From a year to the same year`() {
                assertWholeYear(year1, 5, 0)
            }

            @Test
            fun `From a day to the same year`() {
                assertWholeYear(Day(5), 5, 0)
            }

            @Test
            fun `From a year to the next year`() {
                assertWholeYear(year1, 10, 1)
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
    }
}