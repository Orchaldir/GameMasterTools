package at.orchaldir.gm.core.model.time

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DisplayYearTest {

    @Nested
    inner class DecadeTest {

        @Test
        fun `Test the first decade of AD`() {
            // only 9 years
            assertYear(1, 0, 1, 0)
            assertYear(1, 1, 1, 0)
            assertYear(1, 2, 1, 0)
            assertYear(1, 3, 1, 0)
            assertYear(1, 4, 1, 0)
            assertYear(1, 5, 1, 0)
            assertYear(1, 6, 1, 0)
            assertYear(1, 7, 1, 0)
            assertYear(1, 8, 1, 0)
        }

        @Test
        fun `Test the second decade of AD`() {
            assertYear(1, 9, 1, 1)
            assertYear(1, 10, 1, 1)
            assertYear(1, 11, 1, 1)
            assertYear(1, 12, 1, 1)
            assertYear(1, 13, 1, 1)
            assertYear(1, 14, 1, 1)
            assertYear(1, 15, 1, 1)
            assertYear(1, 16, 1, 1)
            assertYear(1, 17, 1, 1)
            assertYear(1, 18, 1, 1)
        }

        private fun assertYear(inputEra: Int, inputYear: Int, resultEra: Int, resultDecade: Int) {
            assertEquals(DisplayDecade(resultEra, resultDecade), DisplayYear(inputEra, inputYear).decade())
        }
    }

}