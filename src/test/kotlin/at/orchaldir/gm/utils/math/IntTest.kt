package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IntTest {
    @Nested
    inner class CeilDivTest {

        @Test
        fun `An integer result doesn't change`() {
            assertEquals(2, 6.ceilDiv(3))
        }

        @Test
        fun `Round up`() {
            assertEquals(2, 5.ceilDiv(3))
            assertEquals(2, 4.ceilDiv(3))
        }

        @Test
        fun `An negative integer result doesn't change`() {
            assertEquals(-2, (-6).ceilDiv(3))
        }

        @Test
        fun `Round up negative number`() {
            assertEquals(-1, (-5).ceilDiv(3))
            assertEquals(-1, (-4).ceilDiv(3))
        }
    }
}