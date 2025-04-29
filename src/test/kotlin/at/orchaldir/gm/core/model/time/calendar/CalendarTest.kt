package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.DAY_NAME0
import at.orchaldir.gm.DAY_NAME1
import at.orchaldir.gm.DAY_NAME2
import at.orchaldir.gm.NAME0
import at.orchaldir.gm.NAME1
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Year
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalendarTest {

    private val MONTH0 = MonthDefinition(NAME0, 2)
    private val MONTH1 = MonthDefinition(NAME1, 3)
    private val CALENDAR0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(MONTH0, MONTH1)))
    val weekdays = Weekdays(listOf(WeekDay(DAY_NAME0), WeekDay(DAY_NAME1), WeekDay(DAY_NAME2)))
    private val CALENDAR1 = Calendar(CalendarId(1), days = weekdays)
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
                test(CALENDAR0, -5)
            }

            @Test
            fun `Get months of the first year`() {
                test(CALENDAR0, 0)
            }

            @Test
            fun `Get months of the second year`() {
                test(CALENDAR0, 5)
            }

            @Test
            fun `Ignore offset`() {
                val calendar = CALENDAR0.copy(eras = CalendarEras("BC", true, Day(1), "AD", false))

                test(calendar, 0)
            }

            private fun test(calendar: Calendar, startDay: Int) {
                assertEquals(MONTH0, calendar.getMonth(Day(startDay)))
                assertEquals(MONTH0, calendar.getMonth(Day(startDay + 1)))
                assertEquals(MONTH1, calendar.getMonth(Day(startDay + 2)))
                assertEquals(MONTH1, calendar.getMonth(Day(startDay + 3)))
                assertEquals(MONTH1, calendar.getMonth(Day(startDay + 4)))
            }
        }

        @Test
        fun `Get last month index`() {
            assertEquals(1, CALENDAR0.getLastMonthIndex())
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