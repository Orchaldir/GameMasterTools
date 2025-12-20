package at.orchaldir.gm.utils.math.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class VolumeTest {
    val prefixes = SiPrefix.entries - SiPrefix.Kilo

    @Test
    fun `To string`() {
        assertToString(1, "1 mm^3")
        assertToString(12, "12 mm^3")
        assertToString(123, "123 mm^3")
        assertToString(1234, "1.2 cm^3")
        assertToString(12345, "12.3 cm^3")
        assertToString(123456, "123.5 cm^3")
        assertToString(1234567, "1.2 dm^3")
        assertToString(12345678, "12.3 dm^3")
        assertToString(123456789, "123.5 dm^3")
        assertToString(1234567890, "1.2 m^3")
        assertToString(12345678900, "12.3 m^3")
        assertToString(123456789000, "123.5 m^3")
        assertToString(1234567890000, "1234.6 m^3")
    }

    private fun assertToString(cmm: Long, result: String) {
        assertEquals(result, Volume.fromCubicMillimeters(cmm).toString())
    }

    @Nested
    inner class ConversionTest {

        @Test
        fun `Convert to & from Cubic Kilometers`() {
            assertEquals(1, convertToCubicKilometers(convertFromCubicKilometers(1)).toLong())
        }

        @Test
        fun `Convert to & from Cubic Meters`() {
            assertEquals(1, convertToCubicMeters(convertFromCubicMeters(1)).toLong())
        }

        @Test
        fun `Convert to & from Cubic Decimeters`() {
            assertEquals(1, convertToCubicDecimeters(convertFromCubicDecimeters(1)).toLong())
        }

        @Test
        fun `Convert to & from Cubic Centimeters`() {
            assertEquals(1, convertToCubicCentimeters(convertFromCubicCentimeters(1)).toLong())
        }

        @Test
        fun `Convert to & from Cubic Micrometers`() {
            assertEquals(1, convertFromCubicMicrometers(convertToCubicMicrometers(1)).toLong())
        }
    }

}