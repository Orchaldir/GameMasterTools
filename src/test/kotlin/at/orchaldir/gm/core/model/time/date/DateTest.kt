package at.orchaldir.gm.core.model.time.date

import at.orchaldir.gm.CALENDAR0
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DateTest {

    @Nested
    inner class IsOverlappingTest {
        val millennium = Millennium(1)
        val century = Century(19)
        val decade = Decade(190)
        val year = Year(1907)

        @Nested
        inner class YearTest {

            @Test
            fun `Before a decade`() {
                val before = Year(1898)

                assertFalse(decade.isOverlapping(CALENDAR0, before))
                assertFalse(before.isOverlapping(CALENDAR0, decade))
            }

            @Test
            fun `After a decade`() {
                val after = Year(1909)

                assertFalse(decade.isOverlapping(CALENDAR0, after))
                assertFalse(after.isOverlapping(CALENDAR0, decade))
            }

            @Test
            fun `Overlaps itself`() {
                assertTrue(year.isOverlapping(CALENDAR0, year))
            }

            @Test
            fun `Overlaps a decade`() {
                assertTrue(year.isOverlapping(CALENDAR0, decade))
            }

            @Test
            fun `Overlaps a century`() {
                assertTrue(year.isOverlapping(CALENDAR0, century))
            }

            @Test
            fun `Overlaps a millennium`() {
                assertTrue(year.isOverlapping(CALENDAR0, millennium))
            }
        }

        @Nested
        inner class DecadeTest {

            @Test
            fun `Overlaps a year`() {
                assertTrue(decade.isOverlapping(CALENDAR0, year))
            }

            @Test
            fun `Overlaps itself`() {
                assertTrue(decade.isOverlapping(CALENDAR0, decade))
            }

            @Test
            fun `Overlaps a century`() {
                assertTrue(decade.isOverlapping(CALENDAR0, century))
            }

            @Test
            fun `Overlaps a millennium`() {
                assertTrue(decade.isOverlapping(CALENDAR0, millennium))
            }
        }

        @Nested
        inner class CenturyTest {

            @Test
            fun `Overlaps a year`() {
                assertTrue(century.isOverlapping(CALENDAR0, year))
            }

            @Test
            fun `Overlaps a decade`() {
                assertTrue(century.isOverlapping(CALENDAR0, decade))
            }

            @Test
            fun `Overlaps itself`() {
                assertTrue(century.isOverlapping(CALENDAR0, century))
            }

            @Test
            fun `Overlaps a millennium`() {
                assertTrue(century.isOverlapping(CALENDAR0, millennium))
            }
        }

        @Nested
        inner class MillenniumTest {

            @Test
            fun `Overlaps a year`() {
                assertTrue(millennium.isOverlapping(CALENDAR0, year))
            }

            @Test
            fun `Overlaps a decade`() {
                assertTrue(millennium.isOverlapping(CALENDAR0, decade))
            }

            @Test
            fun `Overlaps a century`() {
                assertTrue(millennium.isOverlapping(CALENDAR0, century))
            }

            @Test
            fun `Overlaps itself`() {
                assertTrue(millennium.isOverlapping(CALENDAR0, millennium))
            }
        }

    }
}