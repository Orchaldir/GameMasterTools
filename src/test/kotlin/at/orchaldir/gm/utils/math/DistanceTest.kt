package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DistanceTest {

    @Test
    fun `To string`() {
        assertEquals("0.001 m", Distance(1).toString())
        assertEquals("0.012 m", Distance(12).toString())
        assertEquals("0.123 m", Distance(123).toString())
        assertEquals("1.234 m", Distance(1234).toString())
        assertEquals("12.345 m", Distance(12345).toString())
    }

}