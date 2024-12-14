package at.orchaldir.gm.core.model.time

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DisplayYearTest {

    @Nested
    inner class DecadeTest {

        @Test
        fun `Test the first decade of BC`() {
            // only 9 years
            assertYear(0, 0, 0, 0)
            assertYear(0, 1, 0, 0)
            assertYear(0, 2, 0, 0)
            assertYear(0, 3, 0, 0)
            assertYear(0, 4, 0, 0)
            assertYear(0, 5, 0, 0)
            assertYear(0, 6, 0, 0)
            assertYear(0, 7, 0, 0)
            assertYear(0, 8, 0, 0)
        }

        @Test
        fun `Test the second decade of BC`() {
            assertYearsInDecade(0, 9, 0, 1)
        }

        @Test
        fun `Test a decade of BC`() {
            assertYearsInDecade(0, 109, 0, 11)
        }

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
            assertYearsInDecade(1, 9, 1, 1)
        }

        @Test
        fun `Test a decade of AD`() {
            assertYearsInDecade(1, 1919, 1, 192)
        }

        private fun assertYearsInDecade(inputEra: Int, startYear: Int, resultEra: Int, resultDecade: Int) {
            repeat(10) {
                assertYear(inputEra, startYear + it, resultEra, resultDecade)
            }
        }

        private fun assertYear(inputEra: Int, inputYear: Int, resultEra: Int, resultDecade: Int) {
            assertEquals(DisplayDecade(resultEra, resultDecade), DisplayYear(inputEra, inputYear).decade())
        }
    }

}