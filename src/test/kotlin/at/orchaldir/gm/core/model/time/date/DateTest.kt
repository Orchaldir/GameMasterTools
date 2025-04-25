package at.orchaldir.gm.core.model.time.date

import at.orchaldir.gm.CALENDAR0
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DateTest {

    @Nested
    inner class IsOverlappingTest {
        val decade = Decade(190)
        val year = Year(1907)

        @Test
        fun `A year before the decade`() {
            val before = Year(1898)

            assertFalse(decade.isOverlapping(CALENDAR0, before))
            assertFalse(before.isOverlapping(CALENDAR0, decade))
        }

        @Test
        fun `A year after the decade`() {
            val after = Year(1909)

            assertFalse(decade.isOverlapping(CALENDAR0, after))
            assertFalse(after.isOverlapping(CALENDAR0, decade))
        }

        @Test
        fun `Decade overlaps year`() {
            assertTrue(decade.isOverlapping(CALENDAR0, year))
        }

        @Test
        fun `Year overlaps decade`() {
            assertTrue(year.isOverlapping(CALENDAR0, decade))
        }

    }
}