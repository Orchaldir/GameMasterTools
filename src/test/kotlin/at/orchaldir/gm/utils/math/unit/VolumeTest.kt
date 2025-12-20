package at.orchaldir.gm.utils.math.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VolumeTest {
    val prefixes = SiPrefix.entries - SiPrefix.Kilo

    @Test
    fun `To string`() {
        assertEquals("1 mm^3", Volume.fromCubicMillimeters(1).toString())
        assertEquals("12 mm^3", Volume.fromCubicMillimeters(12).toString())
        assertEquals("123 mm^3", Volume.fromCubicMillimeters(123).toString())
        assertEquals("1.2 cm^3", Volume.fromCubicMillimeters(1234).toString())
        assertEquals("12.3 cm^3", Volume.fromCubicMillimeters(12345).toString())
        assertEquals("123.5 cm^3", Volume.fromCubicMillimeters(123456).toString())
        assertEquals("1.2 dm^3", Volume.fromCubicMillimeters(1234567).toString())
        assertEquals("12.3 dm^3", Volume.fromCubicMillimeters(12345678).toString())
        assertEquals("123.5 dm^3", Volume.fromCubicMillimeters(123456789).toString())
        assertEquals("1.2 m^3", Volume.fromCubicMillimeters(1234567890).toString())
        assertEquals("12.3 m^3", Volume.fromCubicMillimeters(12345678900).toString())
        assertEquals("123.5 m^3", Volume.fromCubicMillimeters(123456789000).toString())
        assertEquals("1234.6 m^3", Volume.fromCubicMillimeters(1234567890000).toString())
    }

    @Test
    fun `Convert to & from si prefix`() {
        val value = 1234L

        prefixes.forEach {
            assertEquals(value, Volume.from(it, value).convertToLong(it))
        }
    }

}