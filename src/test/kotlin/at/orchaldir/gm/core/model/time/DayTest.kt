package at.orchaldir.gm.core.model.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val DAY0 = Day(-10)
private val DAY1 = Day(10)
private val DAY2 = Day(15)

class DayTest {

    @Test
    fun `Compare 2 days`() {
        assertEquals(-1, DAY1.compareTo(DAY2))
        assertEquals(0, DAY1.compareTo(DAY1))
        assertEquals(1, DAY1.compareTo(DAY0))
    }

    @Nested
    inner class GetDurationBetweenTest {

        @Test
        fun `0 days between the same day`() {
            assertDuration(DAY0, DAY0, 0)
            assertDuration(DAY1, DAY1, 0)
            assertDuration(DAY2, DAY2, 0)
        }

        @Test
        fun `Duration from earlier to later day`() {
            assertDuration(DAY0, DAY1, 20)
            assertDuration(DAY0, DAY2, 25)
            assertDuration(DAY1, DAY2, 5)
        }

        @Test
        fun `Duration from later to earlier day`() {
            assertDuration(DAY1, DAY0, 20)
            assertDuration(DAY2, DAY0, 25)
            assertDuration(DAY2, DAY1, 5)
        }

        private fun assertDuration(start: Day, end: Day, duration: Int) {
            assertEquals(Duration(duration), start.getDurationBetween(end))
        }
    }
}