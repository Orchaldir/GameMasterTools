package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.renderer.model.RGB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RGBTest {

    @Test
    fun `Test 2nd constructor with too big values`() {
        assertEquals(RGB(0, 128, 64), RGB(256, 128, 64))
    }

    @Test
    fun `Test the color's hex code`() {
        assertEquals("#ff8000", RGB(255, 128, 0).toCode())
    }

}