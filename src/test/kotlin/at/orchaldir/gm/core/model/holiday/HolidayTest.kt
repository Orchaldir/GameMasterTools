package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.time.DisplayDay
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val CALENDAR_ID0 = CalendarId(0)
private val WEEKDAYS = Weekdays(listOf(WeekDay("d0"), WeekDay("d1")))
private val MONTHS = listOf(MonthDefinition("M0", 2), MonthDefinition("M1", 3))
private val CALENDAR0 = Calendar(CALENDAR_ID0, "C0", WEEKDAYS, months = MONTHS)

class HolidayTest {

    @Nested
    inner class FixedDayInYearTest {

        @Test
        fun `Is on fixed date`() {
            val relativeDate = FixedDayInYear(1, 1)

            assertIsOn(relativeDate, 0, 0, false)
            assertIsOn(relativeDate, 0, 1, false)
            assertIsOn(relativeDate, 1, 0, false)
            assertIsOn(relativeDate, 1, 1, true)
            assertIsOn(relativeDate, 1, 2, false)
        }

        private fun assertIsOn(relativeDate: FixedDayInYear, monthIndex: Int, dayIndex: Int, result: Boolean) {
            assertEquals(result, relativeDate.isOn(CALENDAR0, DisplayDay(dayIndex, 0, monthIndex, dayIndex)))
        }
    }

}