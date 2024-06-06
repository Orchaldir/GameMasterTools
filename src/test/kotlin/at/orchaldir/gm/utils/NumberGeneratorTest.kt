package at.orchaldir.gm.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NumberGeneratorTest {

    @Nested
    inner class FixedNumberGeneratorTest {

        @Test
        fun `Start at index 0 by default`() {
            val generator = FixedNumberGenerator(listOf(9u, 3u, 5u))

            assertNumbers(generator, listOf(9u, 3u, 5u, 9u))
        }

        @Test
        fun `Start at any index`() {
            val generator = FixedNumberGenerator(listOf(9u, 3u, 5u), 2)

            assertNumbers(generator, listOf(5u, 9u, 3u, 5u, 9u))
        }

        private fun assertNumbers(generator: NumberGenerator, numbers: List<UInt>) {
            numbers.forEach { assertEquals(it, generator.getNumber()) }
        }
    }

}