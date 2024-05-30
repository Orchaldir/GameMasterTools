package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.appearance.Color.Blue
import at.orchaldir.gm.core.model.appearance.Color.Red
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.LineOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SvgTest {

    private val options = FillAndBorder(Blue.toRender(), LineOptions(Red.toRender(), 5u))

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
    fun `Render a rectangle`() {
        val aabb = AABB(Point2d(100, 200), Size2d(20, 40))
        val builder = SvgBuilder.create(Size2d(100, 150))
        builder.renderRectangle(aabb, options)
        val svg = builder.finish()

        assertEquals(
            """<svg viewBox="0 0 100 150" xmlns="http://www.w3.org/2000/svg">
  <rect x="100" y="200" width="20" height="40" style="fill:blue;stroke:red;stroke-width:5"/>
</svg>""",
            svg.export()
        )
    }

    @Nested
    inner class DeleteTest {

        @Test
        fun `Render a circle`() {
            val builder = SvgBuilder.create(Size2d(100, 150))
            builder.renderCircle(Point2d(110, 220), 10u, options)

            testCircle(builder)
        }

        @Test
        fun `Render an aabb as a circle`() {
            val aabb = AABB(Point2d(100, 200), Size2d(20, 40))
            val builder = SvgBuilder.create(Size2d(100, 150))
            builder.renderCircle(aabb, options)

            testCircle(builder)
        }

        private fun testCircle(builder: SvgBuilder) {
            val svg = builder.finish()

            assertEquals(
                """<svg viewBox="0 0 100 150" xmlns="http://www.w3.org/2000/svg">
  <circle cx="110" cy="220" r="10" style="fill:blue;stroke:red;stroke-width:5"/>
</svg>""",
                svg.export()
            )
        }
    }

}