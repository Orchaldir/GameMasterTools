package at.orchaldir.gm.utils.math.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AreaTest {
    val prefixes = SiPrefix.entries - SiPrefix.Kilo

    @Test
    fun `To string`() {
        assertToString(1, "1 mm^2")
        assertToString(12, "12 mm^2")
        assertToString(123, "1.2 cm^2")
        assertToString(1234, "12.3 cm^2")
        assertToString(12345, "1.2 dm^2")
        assertToString(123456, "12.3 dm^2")
        assertToString(1234567, "1.2 m^2")
        assertToString(12345678, "12.3 m^2")
        assertToString(123456789, "123.5 m^2")
        assertToString(1234567890, "1234.6 m^2")
        assertToString(12345678900, "12345.7 m^2")
        assertToString(123456789000, "123456.8 m^2")
    }

    private fun assertToString(cmm: Long, result: String) {
        val area = Area.fromSquareMillimeters(cmm)
        assertEquals(result, area.toString())
    }

    @Nested
    inner class ConversionTest {

        @Test
        fun `Convert to & from Square Meters`() {
            assertEquals(1, convertToSquareMeters(convertFromSquareMeters(1)).toLong())
        }

        @Test
        fun `Convert to & from Square Decimeters`() {
            assertEquals(1, convertToSquareDecimeters(convertFromSquareDecimeters(1)).toLong())
        }

        @Test
        fun `Convert to & from Square Centimeters`() {
            assertEquals(1, convertToSquareCentimeters(convertFromSquareCentimeters(1)).toLong())
        }

        @Test
        fun `Convert to & from Square Micrometers`() {
            assertEquals(1, convertFromSquareMicrometers(convertToSquareMicrometers(1)).toLong())
        }
    }

}