package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DistanceTest {

    @Test
    fun `To string`() {
        assertEquals("0.001 m", fromMillimeters(1).toString())
        assertEquals("0.012 m", fromMillimeters(12).toString())
        assertEquals("0.123 m", fromMillimeters(123).toString())
        assertEquals("1.234 m", fromMillimeters(1234).toString())
        assertEquals("12.345 m", fromMillimeters(12345).toString())
    }

}