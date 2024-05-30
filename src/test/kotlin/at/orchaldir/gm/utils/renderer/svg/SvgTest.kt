package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.appearance.Color.Blue
import at.orchaldir.gm.core.model.appearance.Color.Red
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.FillAndBorder
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

    @Test
    fun `Test circle`() {
        val options = FillAndBorder(Blue.toRender(), Red.toRender(), 5u)
        val builder = SvgBuilder.create(Size2d(100, 150))
        builder.renderCircle(Point2d(20, 30), 10u, options)
        val svg = builder.finish()

        assertEquals(
            """<svg viewBox="0 0 100 150" xmlns="http://www.w3.org/2000/svg">
  <circle cx="20" cy="30" r="10" style="fill:blue;stroke:red;stroke-width:5"/>
</svg>""",
            svg.export()
        )
    }

}