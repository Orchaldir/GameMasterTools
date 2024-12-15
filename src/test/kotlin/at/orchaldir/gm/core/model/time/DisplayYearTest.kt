package at.orchaldir.gm.core.model.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DisplayYearTest {

    @Nested
    inner class DecadeTest {

        @Test
        fun `Test the first decade of BC`() {
            assertYearsInDecade(0, 0, 0, 0, 9)
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
            assertYearsInDecade(1, 0, 1, 0, 9)
        }

        @Test
        fun `Test the second decade of AD`() {
            assertYearsInDecade(1, 9, 1, 1)
        }

        @Test
        fun `Test a decade of AD`() {
            assertYearsInDecade(1, 1919, 1, 192)
        }

        private fun assertYearsInDecade(
            inputEra: Int,
            startYear: Int,
            resultEra: Int,
            resultDecade: Int,
            years: Int = 10,
        ) {
            repeat(years) {
                assertYear(inputEra, startYear + it, resultEra, resultDecade)
            }
        }

        private fun assertYear(inputEra: Int, inputYear: Int, resultEra: Int, resultDecade: Int) {
            assertEquals(DisplayDecade(resultEra, resultDecade), DisplayYear(inputEra, inputYear).decade())
        }
    }

}