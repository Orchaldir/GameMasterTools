package at.orchaldir.gm.core.model.time.date

import at.orchaldir.gm.CALENDAR0
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DateTest {

    @Nested
    inner class IsOverlappingTest {
        val decade = Decade(190)
        val year = Year(1907)

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