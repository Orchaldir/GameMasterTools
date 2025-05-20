package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.QUOTE_ID_0
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.DayRange
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HasStartDateTest {

    @Nested
    inner class ExistsTest {
        val day0 = Day(-1)
        val day1 = Day(0)
        val day2 = Day(1)
        val earlyRange = DayRange(-10, -1)
        val rangeEndsAt = DayRange(-1, 0)
        val rangeOverlaps = DayRange(-1, 1)
        val rangeStartsAt = DayRange(0, 1)
        val lateRange = DayRange(1, 10)
        val state = State(
            Storage(CALENDAR0),
            data = Data(time = Time(CALENDAR_ID_0)),
        )
        val elementWithNull = Quote(QUOTE_ID_0)
        val elementWithStartDay = Quote(QUOTE_ID_0, date = day1)

        @Test
        fun `Exists element without date at specific day`() {
            assertTrue(state.exists(elementWithNull, day0))
            assertTrue(state.exists(elementWithNull, day1))
            assertTrue(state.exists(elementWithNull, day2))
        }

        @Test
        fun `Exists element without date at unknown day`() {
            assertTrue(state.exists(elementWithNull, null))
        }

        @Test
        fun `Exists element with specific day at specific day`() {
            assertFalse(state.exists(elementWithStartDay, day0))
            assertTrue(state.exists(elementWithStartDay, day1))
            assertTrue(state.exists(elementWithStartDay, day2))
        }

        @Test
        fun `Exists element with specific day at unknown day`() {
            assertTrue(state.exists(elementWithStartDay, null))
        }

        @Nested
        inner class ElementWithSpecificDayVsRangeTest {

            @Test
            fun `Element with specific day doesn't exist if it starts after a range`() {
                assertFalse(state.exists(elementWithStartDay, earlyRange))
            }

            @Test
            fun `Element with specific day exist if it's start overlaps with the range`() {
                assertTrue(state.exists(elementWithStartDay, rangeEndsAt))
                assertTrue(state.exists(elementWithStartDay, rangeOverlaps))
                assertTrue(state.exists(elementWithStartDay, rangeStartsAt))
            }

            @Test
            fun `Element with specific day exist if it starts before the range`() {
                assertTrue(state.exists(elementWithStartDay, lateRange))
            }
        }
    }
}