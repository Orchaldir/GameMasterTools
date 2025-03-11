package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StartAndEndTest {

    private val month0 = Month("a", 2)
    private val month1 = Month("b", 3)
    private val calendar0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1)))

    @Nested
    inner class MonthTest {

        @Test
        fun `Get start of month`() {
            assertEquals(Day(2), calendar0.getStartOfMonth(Day(2)))
            assertEquals(Day(2), calendar0.getStartOfMonth(Day(3)))
            assertEquals(Day(2), calendar0.getStartOfMonth(Day(4)))
        }

        @Test
        fun `Get end of month`() {
            assertEquals(Day(1), calendar0.getEndOfMonth(Day(0)))
            assertEquals(Day(1), calendar0.getEndOfMonth(Day(1)))
        }

        @Nested
        inner class GetStartOfNextMonthTest {
            @Test
            fun `Get start of next month in first era`() {
                assertEquals(Day(-3), calendar0.getStartOfNextMonth(Day(-5)))
                assertEquals(Day(-3), calendar0.getStartOfNextMonth(Day(-4)))
            }

            @Test
            fun `Get start of next month across era`() {
                assertEquals(Day(0), calendar0.getStartOfNextMonth(Day(-3)))
                assertEquals(Day(0), calendar0.getStartOfNextMonth(Day(-2)))
                assertEquals(Day(0), calendar0.getStartOfNextMonth(Day(-1)))
            }

            @Test
            fun `Get start of next month`() {
                assertEquals(Day(2), calendar0.getStartOfNextMonth(Day(0)))
                assertEquals(Day(2), calendar0.getStartOfNextMonth(Day(1)))
            }

            @Test
            fun `In the next year`() {
                assertEquals(Day(5), calendar0.getStartOfNextMonth(Day(2)))
                assertEquals(Day(5), calendar0.getStartOfNextMonth(Day(3)))
                assertEquals(Day(5), calendar0.getStartOfNextMonth(Day(4)))
            }
        }

        @Nested
        inner class GetStartOfPreviousMonthTest {
            @Test
            fun `Get start of previous month in first era`() {
                assertEquals(Day(-5), calendar0.getStartOfPreviousMonth(Day(-3)))
                assertEquals(Day(-5), calendar0.getStartOfPreviousMonth(Day(-2)))
                assertEquals(Day(-5), calendar0.getStartOfPreviousMonth(Day(-1)))
            }

            @Test
            fun `Get start of previous month`() {
                assertEquals(Day(-3), calendar0.getStartOfPreviousMonth(Day(0)))
                assertEquals(Day(-3), calendar0.getStartOfPreviousMonth(Day(1)))
            }

            @Test
            fun `In the next year`() {
                assertEquals(Day(0), calendar0.getStartOfPreviousMonth(Day(2)))
                assertEquals(Day(0), calendar0.getStartOfPreviousMonth(Day(3)))
                assertEquals(Day(0), calendar0.getStartOfPreviousMonth(Day(4)))
            }
        }
    }
}