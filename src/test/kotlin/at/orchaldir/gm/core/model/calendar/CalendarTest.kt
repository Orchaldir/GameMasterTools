package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.core.model.time.*
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

private val MONTH0 = Month("a", 2)
private val MONTH1 = Month("b", 3)
private val CALENDAR0 = Calendar(CalendarId(0), months = listOf(MONTH0, MONTH1))
private val CALENDAR1 = Calendar(CalendarId(1), days = Weekdays(listOf(WeekDay("D0"), WeekDay("D1"), WeekDay("D2"))))

class CalendarTest {

    @Nested
    inner class DisplayTest {

        @Test
        fun `Test a day in AD`() {
            assertEquals("14.12.2024 AD", CALENDAR0.display(DisplayDay(1, 2023, 11, 13)))
        }

        @Test
        fun `Test a year in AD`() {
            assertEquals("2024 AD", CALENDAR0.display(DisplayYear(1, 2023)))
        }

        @Test
        fun `Test a decade in AD`() {
            assertEquals("2020s AD", CALENDAR0.display(DisplayDecade(1, 202)))
        }

        @Test
        fun `Test the first day in AD`() {
            assertEquals("1.1.1 AD", CALENDAR0.display(DisplayDay(1, 0, 0, 0)))
        }

        @Test
        fun `Test the first year in AD`() {
            assertEquals("1 AD", CALENDAR0.display(DisplayYear(1, 0)))
        }

        @Test
        fun `Test the first decade in AD`() {
            // not sure about this
            assertEquals("0s AD", CALENDAR0.display(DisplayDecade(1, 0)))
        }

        @Test
        fun `Test a single digit decade in AD`() {
            assertEquals("50s AD", CALENDAR0.display(DisplayDecade(1, 5)))
        }

        @Test
        fun `Test a day in BC`() {
            assertEquals("BC 14.12.102", CALENDAR0.display(DisplayDay(0, 101, 11, 13)))
        }

        @Test
        fun `Test a year in BC`() {
            assertEquals("BC 1235", CALENDAR0.display(DisplayYear(0, 1234)))
        }

        @Test
        fun `Test a decade in BC`() {
            assertEquals("BC 110s", CALENDAR0.display(DisplayDecade(0, 11)))
        }

        @Test
        fun `Test the first decade in BC`() {
            assertEquals("BC 0s", CALENDAR0.display(DisplayDecade(0, 0)))
        }

    }

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
            @MethodSource("at.orchaldir.gm.core.model.calendar.CalendarTest#provideStartOfYear")
            fun `Get the (start) day of a year`(year: Year, day: Day) {
                assertGetDay(year, day)
            }

            @ParameterizedTest
            @MethodSource("at.orchaldir.gm.core.model.calendar.CalendarTest#provideStartOfDecade")
            fun `Get the (start) day of a decade`(decade: Decade, day: Day) {
                assertGetDay(decade, day)
            }

            private fun assertGetDay(input: Date, day: Day) {
                val display = CALENDAR0.resolve(day)

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
                assertGetYear(Year(2), 2)
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
                val display = CALENDAR0.resolve(year)

                assertEquals(year, CALENDAR0.getYear(input))
                assertEquals(display, CALENDAR0.getDisplayYear(input))
            }
        }

        @ParameterizedTest
        @MethodSource("at.orchaldir.gm.core.model.calendar.CalendarTest#provideStartOfYear")
        fun `Get the start of a year`(year: Year, day: Day) {
            assertEquals(day, CALENDAR0.getStartOfYear(year))
        }

        @Test
        fun `Get the end of a year`() {
            assertEquals(Day(-1), CALENDAR0.getEndOfYear(Year(-1)))
            assertEquals(Day(4), CALENDAR0.getEndOfYear(Year(0)))
            assertEquals(Day(9), CALENDAR0.getEndOfYear(Year(1)))
            assertEquals(Day(14), CALENDAR0.getEndOfYear(Year(2)))
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
            assertEquals(display, CALENDAR0.display(CALENDAR0.resolve(day)))
        }

        @ParameterizedTest
        @MethodSource("at.orchaldir.gm.core.model.calendar.CalendarTest#provideStartOfDecade")
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
                assertEquals(1, CALENDAR0.compareToOptional(Year(2), Year(1)))
            }

            @Test
            fun `Test a equal to b`() {
                assertEquals(0, CALENDAR0.compareToOptional(Year(1), Year(1)))
            }

            @Test
            fun `Test a less than b`() {
                assertEquals(-1, CALENDAR0.compareToOptional(Year(1), Year(2)))
            }

            @Test
            fun `Given a is null, then return 0`() {
                assertEquals(-1, CALENDAR0.compareToOptional(null, Year(1)))
            }

            @Test
            fun `Given b is null, then return 0`() {
                assertEquals(1, CALENDAR0.compareToOptional(Year(1), null))
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
                assertTrue(CALENDAR0.isAfter(Year(2), Year(1)))
            }

            @Test
            fun `Test a equal to b`() {
                assertFalse(CALENDAR0.isAfter(Year(1), Year(1)))
            }

            @Test
            fun `Test a less than b`() {
                assertFalse(CALENDAR0.isAfter(Year(1), Year(2)))
            }
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
        inner class IsAfterOrEqualOptionalTest {
            @Test
            fun `Test a greater than b`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(Year(2), Year(1)))
            }

            @Test
            fun `Test a equal to b`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(Year(1), Year(1)))
            }

            @Test
            fun `Test a less than b`() {
                assertFalse(CALENDAR0.isAfterOrEqualOptional(Year(1), Year(2)))
            }

            @Test
            fun `Test a is null`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(null, Year(1)))
            }

            @Test
            fun `Test b is null`() {
                assertTrue(CALENDAR0.isAfterOrEqualOptional(Year(1), null))
            }
        }
    }

    @Nested
    inner class DurationTest {

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
            assertResolve(CALENDAR0, 0, 1, 0) // 1 AD
            assertResolve(CALENDAR0, 1, 1, 1)
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
            val calendar = createCalendar(Year(1))

            assertResolve(calendar, -2, 0, 2)
            assertResolve(calendar, -1, 0, 1)
            assertResolve(calendar, 0, 0, 0)
            assertResolve(calendar, 1, 1, 0) // 1 AD
            assertResolve(calendar, 2, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputYear: Int, eraIndex: Int, yearIndex: Int) {
            val year = Year(inputYear)
            val displayYear = DisplayYear(eraIndex, yearIndex)

            assertEquals(displayYear, calendar.resolve(year))
            assertEquals(year, calendar.resolve(displayYear))
        }
    }

    @Nested
    inner class ResolveDecadeTest {
        @Test
        fun `Test without offset`() {
            assertResolve(CALENDAR0, -2, 0, 1)
            assertResolve(CALENDAR0, -1, 0, 0)
            assertResolve(CALENDAR0, 0, 1, 0) // 1 AD
            assertResolve(CALENDAR0, 1, 1, 1)
        }

        @Test
        fun `Test with positive offset`() {
            val calendar = createCalendar(Decade(1))

            assertResolve(calendar, -2, 0, 2)
            assertResolve(calendar, -1, 0, 1)
            assertResolve(calendar, 0, 0, 0)
            assertResolve(calendar, 1, 1, 0) // 1 AD
            assertResolve(calendar, 2, 1, 1)
        }

        private fun assertResolve(calendar: Calendar, inputDecade: Int, eraIndex: Int, decadeIndex: Int) {
            val decade = Decade(inputDecade)
            val displayYDecade = DisplayDecade(eraIndex, decadeIndex)

            assertEquals(displayYDecade, calendar.resolve(decade))
            assertEquals(decade, calendar.resolve(displayYDecade))
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

    private fun createCalendar(date: Date) = CALENDAR0.copy(eras = CalendarEras("BC", true, date, "AD", false))
}