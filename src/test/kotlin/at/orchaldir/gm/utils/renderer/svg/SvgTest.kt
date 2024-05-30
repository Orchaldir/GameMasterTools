package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.Size2d
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SvgTest {

    @Test
    fun `Test empty svg`() {
        val builder = SvgBuilder.create(Size2d(100, 150))
        val svg = builder.finish()

        assertEquals(
            "<svg viewBox=\"0 0 100 150\" xmlns=\"http://www.w3.org/2000/svg\">\n</svg>",
            svg.export()
        )
    }

}