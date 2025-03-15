package at.orchaldir.gm.utils.math.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WeightTest {

    @Test
    fun `To string`() {
        assertEquals("0.001 kg", Weight.fromGram(1).toString())
        assertEquals("0.012 kg", Weight.fromGram(12).toString())
        assertEquals("0.123 kg", Weight.fromGram(123).toString())
        assertEquals("1.234 kg", Weight.fromGram(1234).toString())
        assertEquals("12.3 kg", Weight.fromGram(12345).toString())
    }

}