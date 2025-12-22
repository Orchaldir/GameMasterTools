package at.orchaldir.gm.utils.math.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WeightTest {
    val prefixes = SiPrefix.entries - SiPrefix.Micro

    @Test
    fun `To string`() {
        assertEquals("1.0 g", Weight.fromGrams(1).toString())
        assertEquals("12.0 g", Weight.fromGrams(12).toString())
        assertEquals("123.0 g", Weight.fromGrams(123).toString())
        assertEquals("1.0 kg", Weight.fromKilograms(1).toString())
        assertEquals("1.2 kg", Weight.fromGrams(1234).toString())
        assertEquals("12.3 kg", Weight.fromGrams(12345).toString())
    }

    @Test
    fun `Convert to & from si prefix`() {
        val value = 1234L

        prefixes.forEach {
            assertEquals(value, Weight.from(it, value).convertToLong(it))
        }
    }

    @Test
    fun `Calculate from volume`() {
        assertEquals(
            Weight.fromKilograms(50),
            Weight.fromVolume(Volume.fromCubicMeters(2.5f), Weight.fromKilograms(20)),
        )
    }

}