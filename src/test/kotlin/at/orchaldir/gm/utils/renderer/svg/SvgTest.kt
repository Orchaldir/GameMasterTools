package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.appearance.Color.*
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.BorderOnly
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.LineOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SvgTest {

    @Test
    fun `Test empty svg`() {
        val builder = SvgBuilder.create(Size2d(100.0f, 150.0f))
        val svg = builder.finish()

        assertEquals(
            "<svg viewBox=\"0 0 100.000 150.000\" xmlns=\"http://www.w3.org/2000/svg\">\n</svg>",
            svg.export()
        )
    }

    @Test
    fun `Render a rectangle`() {
        val options = BorderOnly(LineOptions(Green.toRender(), 10.0f))
        val aabb = AABB(Point2d(100.0f, 200.0f), Size2d(20.0f, 40.0f))
        val builder = SvgBuilder.create(Size2d(100.0f, 150.0f))
        builder.renderRectangle(aabb, options)
        val svg = builder.finish()

        assertEquals(
            """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <rect x="100.000" y="200.000" width="20.000" height="40.000" style="fill:none;stroke:green;stroke-width:10.000"/>
</svg>""",
            svg.export()
        )
    }

    @Nested
    inner class DeleteTest {

        private val options = FillAndBorder(Blue.toRender(), LineOptions(Red.toRender(), 5.0f))

        @Test
        fun `Render a circle`() {
            val builder = SvgBuilder.create(Size2d(100.0f, 150.0f))
            builder.renderCircle(Point2d(110.0f, 220.0f), Distance(10.0f), options)

            testCircle(builder)
        }

        @Test
        fun `Render an aabb as a circle`() {
            val aabb = AABB(Point2d(100.0f, 200.0f), Size2d(20.0f, 40.0f))
            val builder = SvgBuilder.create(Size2d(100.0f, 150.0f))
            builder.renderCircle(aabb, options)

            testCircle(builder)
        }

        private fun testCircle(builder: SvgBuilder) {
            val svg = builder.finish()

            assertEquals(
                """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <circle cx="110.000" cy="220.000" r="10.000" style="fill:blue;stroke:red;stroke-width:5.000"/>
</svg>""",
                svg.export()
            )
        }
    }

}