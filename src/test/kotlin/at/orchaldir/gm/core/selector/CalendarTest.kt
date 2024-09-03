package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.holiday.FixedDayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.holiday.WeekdayInMonth
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val ID0 = HolidayId(0)
private val HOLIDAY0 = Holiday(ID0, relativeDate = FixedDayInYear(4, 2))
private val HOLIDAY1 = Holiday(ID0, relativeDate = WeekdayInMonth(2, 0, 3))

class CalendarTest {

    @Nested
    inner class GetMinNumberOfDaysTest {

        @Test
        fun `Get default for FixedDayInYear with in month without holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY0), 0))
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY0), 1))
        }

        @Test
        fun `Get correct minimum for FixedDayInYear with in month with holiday`() {
            assertEquals(5, getMinNumberOfDays(listOf(HOLIDAY0), 2))
        }

        @Test
        fun `Get default for WeekdayInMonth with in month without holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY1), 0))
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY1), 1))
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY1), 2))
        }

        @Test
        fun `Get default for WeekdayInMonth with in month with holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY1), 3))
        }
    }

    @Nested
    inner class GetMinNumberOfMonthsTest {

        @Test
        fun `With FixedDayInYear`() {
            assertEquals(3, getMinNumberOfMonths(listOf(HOLIDAY0)))
        }

        @Test
        fun `With WeekdayInMonth`() {
            assertEquals(4, getMinNumberOfMonths(listOf(HOLIDAY1)))
        }
    }

    @Nested
    inner class GetMinNumberOfWeekdaysTest {

        @Test
        fun `With FixedDayInYear`() {
            assertEquals(2, getMinNumberOfWeekdays(listOf(HOLIDAY0)))
        }

        @Test
        fun `With WeekdayInMonth`() {
            assertEquals(3, getMinNumberOfWeekdays(listOf(HOLIDAY1)))
        }
    }

    @Nested
    inner class SupportsDayOfTheMonthTest {

        @Test
        fun `FixedDayInYear supports DayOfTheMonth`() {
            assertTrue(supportsDayOfTheMonth(listOf(HOLIDAY0)))
        }

        @Test
        fun `WeekdayInMonth doesn't support DayOfTheMonth`() {
            assertFalse(supportsDayOfTheMonth(listOf(HOLIDAY1)))
        }

        @Test
        fun `Mixed doesn't support DayOfTheMonth`() {
            assertFalse(supportsDayOfTheMonth(listOf(HOLIDAY0, HOLIDAY1)))
        }
    }

}