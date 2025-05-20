package at.orchaldir.gm.core.selector.time.calendar

import at.orchaldir.gm.HOLIDAY_ID_0
import at.orchaldir.gm.core.model.time.holiday.DayInYear
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.WeekdayInMonth
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalendarTest {

    private val FIXED_DAY_HOLIDAY = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(4, 2))
    private val WEEKDAY_HOLIDAY = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(2, 0, 3))

    @Nested
    inner class GetMinNumberOfDaysTest {

        @Test
        fun `Get default for DayInYear with in month without holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(FIXED_DAY_HOLIDAY), 0))
            assertEquals(2, getMinNumberOfDays(listOf(FIXED_DAY_HOLIDAY), 1))
        }

        @Test
        fun `Get correct minimum for DayInYear with in month with holiday`() {
            assertEquals(5, getMinNumberOfDays(listOf(FIXED_DAY_HOLIDAY), 2))
        }

        @Test
        fun `Get default for WeekdayInMonth with in month without holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(WEEKDAY_HOLIDAY), 0))
            assertEquals(2, getMinNumberOfDays(listOf(WEEKDAY_HOLIDAY), 1))
            assertEquals(2, getMinNumberOfDays(listOf(WEEKDAY_HOLIDAY), 2))
        }

        @Test
        fun `Get default for WeekdayInMonth with in month with holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(WEEKDAY_HOLIDAY), 3))
        }
    }

    @Nested
    inner class GetMinNumberOfMonthsTest {

        @Test
        fun `With DayInYear`() {
            assertEquals(3, getMinNumberOfMonths(listOf(FIXED_DAY_HOLIDAY)))
        }

        @Test
        fun `With WeekdayInMonth`() {
            assertEquals(4, getMinNumberOfMonths(listOf(WEEKDAY_HOLIDAY)))
        }
    }

    @Nested
    inner class GetMinNumberOfWeekdaysTest {

        @Test
        fun `With DayInYear`() {
            assertEquals(2, getMinNumberOfWeekdays(listOf(FIXED_DAY_HOLIDAY)))
        }

        @Test
        fun `With WeekdayInMonth`() {
            assertEquals(3, getMinNumberOfWeekdays(listOf(WEEKDAY_HOLIDAY)))
        }
    }

    @Nested
    inner class SupportsDayOfTheMonthTest {

        @Test
        fun `DayInYear supports DayOfTheMonth`() {
            assertTrue(supportsDayOfTheMonth(listOf(FIXED_DAY_HOLIDAY)))
        }

        @Test
        fun `WeekdayInMonth doesn't support DayOfTheMonth`() {
            assertFalse(supportsDayOfTheMonth(listOf(WEEKDAY_HOLIDAY)))
        }

        @Test
        fun `Mixed doesn't support DayOfTheMonth`() {
            assertFalse(supportsDayOfTheMonth(listOf(FIXED_DAY_HOLIDAY, WEEKDAY_HOLIDAY)))
        }
    }

}