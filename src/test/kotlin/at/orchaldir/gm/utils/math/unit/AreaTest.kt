package at.orchaldir.gm.utils.math.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AreaTest {
    val prefixes = SiPrefix.entries - SiPrefix.Kilo

    @Test
    fun `To string`() {
        assertToString(1.2f, "1.2 m^2")
        assertToString(12.3f, "12.3 m^2")
        assertToString(123.4f, "123.4 m^2")
        assertToString(1234.5f, "1234.5 m^2")
        assertToString(12345.6f, "12345.6 m^2")
        assertToString(123456.7f, "123456.7 m^2")
        assertToString(1234567.8f, "1.2 km^2")
        assertToString(12345679.0f, "12.3 km^2")
    }

    private fun assertToString(sm: Float, result: String) {
        val area = Area.fromSquareMeters(sm)
        assertEquals(result, area.toString())
    }

    @Nested
    inner class ConversionTest {

        @Test
        fun `Convert to & from Square Kilometers`() {
            assertEquals(1.5f, convertFromSquareKilometers(convertToSquareKilometers(1.5f)), 0.001f)
        }
    }

}