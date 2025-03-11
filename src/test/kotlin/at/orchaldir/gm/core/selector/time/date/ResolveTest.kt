package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResolveTest {

    private val month0 = Month("A", 1)
    private val month1 = Month("B", 2)
    private val month2 = Month("C", 3)
    private val calendar = Calendar(
        CALENDAR_ID_0,
        days = Weekdays(listOf(WeekDay("a"), WeekDay("b"))),
        months = ComplexMonths(listOf(month0, month1, month2)),
    )


    @Nested
    inner class ResolveDayTest {

        @Test
        fun `Resolve the first month the first year in AD`() {
            assertEquals(
                DisplayDay(1, 0, 0, 0, 0),
                calendar.resolve(Day(0))
            )
        }

        @Test
        fun `Resolve the second month the first year in AD`() {
            assertEquals(
                DisplayDay(1, 0, 1, 0, 1),
                calendar.resolve(Day(1))
            )
            assertEquals(
                DisplayDay(1, 0, 1, 1, 0),
                calendar.resolve(Day(2))
            )
        }

        @Test
        fun `Resolve the third month the first year in AD`() {
            assertEquals(
                DisplayDay(1, 0, 2, 0, 1),
                calendar.resolve(Day(3))
            )
            assertEquals(
                DisplayDay(1, 0, 2, 1, 0),
                calendar.resolve(Day(4))
            )
            assertEquals(
                DisplayDay(1, 0, 2, 2, 1),
                calendar.resolve(Day(5))
            )
        }

    }


}