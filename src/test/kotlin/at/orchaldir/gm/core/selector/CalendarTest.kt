package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.holiday.FixedDayInYear
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.holiday.WeekdayInMonth
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val ID0 = HolidayId(0)
private val HOLIDAY0 = Holiday(ID0, relativeDate = FixedDayInYear(4, 1))
private val HOLIDAY1 = Holiday(ID0, relativeDate = WeekdayInMonth(0, 0, 1))

class CalendarTest {

    @Nested
    inner class GetMinNumberOfDaysTest {

        @Test
        fun `Get default for FixedDayInYear with in month without holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY0), 0))
        }

        @Test
        fun `Get correct minimum for FixedDayInYear with in month with holiday`() {
            assertEquals(5, getMinNumberOfDays(listOf(HOLIDAY0), 1))
        }

        @Test
        fun `Get default for WeekdayInMonth with in month without holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY1), 0))
        }

        @Test
        fun `Get default for WeekdayInMonth with in month with holiday`() {
            assertEquals(2, getMinNumberOfDays(listOf(HOLIDAY1), 1))
        }
    }

}