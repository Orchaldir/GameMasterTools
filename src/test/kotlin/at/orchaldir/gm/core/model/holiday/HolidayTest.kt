package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.DAY_NAME0
import at.orchaldir.gm.DAY_NAME1
import at.orchaldir.gm.NAME0
import at.orchaldir.gm.NAME1
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.holiday.DayInYear
import at.orchaldir.gm.core.model.time.holiday.RelativeDate
import at.orchaldir.gm.core.model.time.holiday.WeekdayInMonth
import at.orchaldir.gm.core.selector.time.date.resolveDay
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val CALENDAR_ID0 = CalendarId(0)
private val WEEKDAYS = Weekdays(listOf(WeekDay(DAY_NAME0), WeekDay(DAY_NAME1)))
private val MONTHS = ComplexMonths(
    listOf(
        MonthDefinition(NAME0, 2),
        MonthDefinition(NAME1, 3)
    )
)
private val CALENDAR0 = Calendar(CALENDAR_ID0, days = WEEKDAYS, months = MONTHS)

class HolidayTest {

    @Nested
    inner class IsOnFixedDayTest {

        private val relativeDate = DayInYear(1, 1)

        @Test
        fun `Year 0`() {
            assertYearForIsOn(relativeDate, 0, 3)
        }

        @Test
        fun `Year 1`() {
            assertYearForIsOn(relativeDate, 1, 3)
        }
    }

    @Nested
    inner class IsOnWeekdayTest {

        private val relativeDate = WeekdayInMonth(1, 0, 1)

        @Test
        fun `Year 0`() {
            assertYearForIsOn(relativeDate, 0, 3)
        }

        @Test
        fun `Year 1`() {
            assertYearForIsOn(relativeDate, 1, 2)
        }

        @Test
        fun `Year 2`() {
            assertYearForIsOn(relativeDate, 2, 3)
        }
    }

    private fun assertYearForIsOn(relativeDate: RelativeDate, yearIndex: Int, holiday: Int) {
        val daysPerYear = CALENDAR0.getDaysPerYear()
        val start = yearIndex * daysPerYear

        repeat(daysPerYear) { index ->
            assertIsOn(relativeDate, start + index, index == holiday)
        }
    }

    private fun assertIsOn(
        relativeDate: RelativeDate,
        dayIndex: Int,
        result: Boolean,
    ) {
        val displayDay = CALENDAR0.resolveDay(Day(dayIndex))
        assertEquals(result, relativeDate.isOn(CALENDAR0, displayDay))
    }

}