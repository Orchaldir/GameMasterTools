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

            assertEquals(9u, generator.getNumber())
            assertEquals(3u, generator.getNumber())
            assertEquals(5u, generator.getNumber())
            assertEquals(9u, generator.getNumber())
        }

        @Test
        fun `Start at any index`() {
            val generator = FixedNumberGenerator(listOf(9u, 3u, 5u), 2)

            assertEquals(5u, generator.getNumber())
            assertEquals(9u, generator.getNumber())
            assertEquals(3u, generator.getNumber())
            assertEquals(5u, generator.getNumber())
            assertEquals(9u, generator.getNumber())
        }
    }

}