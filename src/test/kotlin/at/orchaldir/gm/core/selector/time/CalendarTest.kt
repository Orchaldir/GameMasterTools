package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.CALENDAR_ID_1
import at.orchaldir.gm.CULTURE_ID_0
import at.orchaldir.gm.HOLIDAY_ID_0
import at.orchaldir.gm.LANGUAGE_ID_1
import at.orchaldir.gm.PERIODICAL_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.holiday.DayInYear
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.WeekdayInMonth
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalendarTest {

    private val FIXED_DAY_HOLIDAY = Holiday(HOLIDAY_ID_0, relativeDate = DayInYear(4, 2))
    private val WEEKDAY_HOLIDAY = Holiday(HOLIDAY_ID_0, relativeDate = WeekdayInMonth(2, 0, 3))

    @Nested
    inner class CanDeleteTest {
        private val calendar = Calendar(CALENDAR_ID_0)
        private val state = State(
            listOf(
                Storage(calendar),
            )
        )

        @Test
        fun `Cannot delete a calendar used by a culture`() {
            val culture = Culture(CULTURE_ID_0, calendar = CALENDAR_ID_0)
            val newState = state.updateStorage(Storage(culture))

            failCanDelete(newState, CULTURE_ID_0)
        }

        @Test
        fun `Cannot delete a calendar used by a holiday`() {
            val holiday = Holiday(HOLIDAY_ID_0, calendar = CALENDAR_ID_0)
            val newState = state.updateStorage(Storage(holiday))

            failCanDelete(newState, HOLIDAY_ID_0)
        }

        @Test
        fun `Cannot delete a calendar used by a periodical`() {
            val periodical = Periodical(PERIODICAL_ID_0, calendar = CALENDAR_ID_0)
            val newState = state.updateStorage(Storage(periodical))

            failCanDelete(newState, PERIODICAL_ID_0)
        }

        @Test
        fun `Cannot delete a calendar with a child`() {
            val calendar1 = Calendar(CALENDAR_ID_1, origin = ModifiedElement(CALENDAR_ID_0))
            val newState = state.updateStorage(Storage(listOf(calendar, calendar1)))

            failCanDelete(newState, CALENDAR_ID_1)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(CALENDAR_ID_0).addId(blockingId), state.canDeleteCalendar(CALENDAR_ID_0))
        }
    }

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