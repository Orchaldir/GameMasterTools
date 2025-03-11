package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.core.selector.time.date.resolveDay
import at.orchaldir.gm.core.selector.time.date.displayYear
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
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
        inner class GetDayTest {

            @Test
            fun `Given a day, returns the same day`() {
                val day = Day(99)
                assertGetDay(day, day)
            }

            @ParameterizedTest
            @MethodSource("at.orchaldir.gm.core.model.time.calendar.CalendarTest#provideStartOfYear")
            fun `Get the (start) day of a year`(year: Year, day: Day) {
                assertGetDay(year, day)
            }

            @ParameterizedTest
            @MethodSource("at.orchaldir.gm.core.model.time.calendar.CalendarTest#provideStartOfDecade")
            fun `Get the (start) day of a decade`(decade: Decade, day: Day) {
                assertGetDay(decade, day)
            }

            private fun assertGetDay(input: Date, day: Day) {
                val display = CALENDAR0.resolveDay(day)

                assertEquals(day, CALENDAR0.getDay(input))
                assertEquals(display, CALENDAR0.getDisplayDay(input))
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
                val display = CALENDAR0.displayYear(year)

                assertEquals(year, CALENDAR0.getYear(input))
                assertEquals(display, CALENDAR0.getDisplayYear(input))
            }
        }

        @ParameterizedTest
        @MethodSource("at.orchaldir.gm.core.model.time.calendar.CalendarTest#provideStartOfYear")
        fun `Get the start of a year`(year: Year, day: Day) {
            assertEquals(day, CALENDAR0.getStartOfYear(year))
        }

        @Test
        fun `Get the end of a year`() {
            assertEquals(Day(-1), CALENDAR0.getEndOfYear(Year(-1)))
            assertEquals(Day(4), CALENDAR0.getEndOfYear(year0))
            assertEquals(Day(9), CALENDAR0.getEndOfYear(year1))
            assertEquals(Day(14), CALENDAR0.getEndOfYear(year2))
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

        @Test
        fun `Display the start & end of a positive decade`() {
            val decade = Decade(186)

            assertDisplay(CALENDAR0.getStartOfDecade(decade), "1.1.1860 AD")
            assertDisplay(CALENDAR0.getEndOfDecade(decade), "3.2.1869 AD")
        }

        @Test
        fun `The AD 0s only have 9 years`() {
            val decade = Decade(0)

            assertDisplay(CALENDAR0.getStartOfDecade(decade), "1.1.1 AD")
            assertDisplay(CALENDAR0.getEndOfDecade(decade), "3.2.9 AD")
        }

        @Test
        fun `The 0s BC only have 9 years`() {
            val decade = Decade(-1)

            assertDisplay(CALENDAR0.getStartOfDecade(decade), "BC 1.1.9")
            assertDisplay(CALENDAR0.getEndOfDecade(decade), "BC 3.2.1")
        }

        @Test
        fun `Display the start & end of a negative decade`() {
            val decade = Decade(-6)

            assertDisplay(CALENDAR0.getStartOfDecade(decade), "BC 1.1.59")
            assertDisplay(CALENDAR0.getEndOfDecade(decade), "BC 3.2.50")
        }

        private fun assertDisplay(day: Day, display: String) {
            assertEquals(display, display(CALENDAR0, day))
        }

        @ParameterizedTest
        @MethodSource("at.orchaldir.gm.core.model.time.calendar.CalendarTest#provideStartOfDecade")
        fun `Get the start of a decade`(decade: Decade, day: Day) {
            assertEquals(day, CALENDAR0.getStartOfDecade(decade))
        }

        @Test
        fun `Get the end of a negative decade`() {
            assertEquals(Day(-46), CALENDAR0.getEndOfDecade(Decade(-2)))
            assertEquals(Day(-1), CALENDAR0.getEndOfDecade(Decade(-1)))
        }

        @Test
        fun `Get the end of a decade`() {
            assertEquals(Day(44), CALENDAR0.getEndOfDecade(Decade(0)))
            assertEquals(Day(94), CALENDAR0.getEndOfDecade(Decade(1)))
            assertEquals(Day(144), CALENDAR0.getEndOfDecade(Decade(2)))
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

    companion object {
        @JvmStatic
        fun provideStartOfYear(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Year(-2), Day(-10)),
                Arguments.of(Year(-1), Day(-5)),
                Arguments.of(Year(0), Day(0)),
                Arguments.of(Year(1), Day(5)),
                Arguments.of(Year(2), Day(10)),
            )
        }

        @JvmStatic
        fun provideStartOfDecade(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Decade(-2), Day(-95)),
                Arguments.of(Decade(-1), Day(-45)),
                Arguments.of(Decade(0), Day(0)),
                Arguments.of(Decade(1), Day(45)),
                Arguments.of(Decade(2), Day(95)),
            )
        }
    }
}