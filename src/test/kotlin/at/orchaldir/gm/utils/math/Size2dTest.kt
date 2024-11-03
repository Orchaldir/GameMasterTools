package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Size2dTest {

    @Test
    fun `Create square`() {
        val result = Size2d(5.0f, 5.0f)

        assertEquals(result, Size2d.square(5.0f))
        assertEquals(result, Size2d.square(Distance(5000)))
    }

    @Test
    fun `Scale size`() {
        val input = Size2d(10.0f, 20.0f)
        val result = Size2d(15.0f, 60.0f)

        assertEquals(result, input.scale(Factor(1.5f), Factor(3.0f)))
    }

}