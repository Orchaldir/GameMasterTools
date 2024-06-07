package at.orchaldir.gm.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NumberGeneratorTest {

    @Nested
    inner class SelectTest {

        @Test
        fun `Select a random element from a list`() {
            val generator = Counter()
            val list = listOf(10, 11, 12)

            assertEquals(10, generator.select(list))
            assertEquals(11, generator.select(list))
            assertEquals(12, generator.select(list))
            assertEquals(10, generator.select(list))
        }

    }

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
    }

    @Nested
    inner class CounterTest {

        @Test
        fun `Start at index 0 by default`() {
            val generator = Counter()

            assertNumbers(generator, listOf(0u, 1u, 2u, 3u))
        }

        @Test
        fun `Start at any index`() {
            val generator = Counter(5u)

            assertNumbers(generator, listOf(5u, 6u, 7u, 8u))
        }

    }


    private fun assertNumbers(generator: NumberGenerator, numbers: List<UInt>) {
        numbers.forEach { assertEquals(it, generator.getNumber()) }
    }
}