package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.util.Color.*
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.zero
import at.orchaldir.gm.utils.renderer.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SvgTest {

    private val options = FillAndBorder(Blue.toRender(), LineOptions(Red.toRender(), 5.0f))

    @Test
    fun `Test empty svg`() {
        val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))
        val svg = builder.finish()

        assertEquals(
            "<svg viewBox=\"0 0 100.000 150.000\" xmlns=\"http://www.w3.org/2000/svg\">\n</svg>",
            svg.export()
        )
    }

    @Test
    fun `Render a rectangle`() {
        val options = BorderOnly(LineOptions(Green.toRender(), 10.0f))
        val aabb = AABB.fromMeters(100.0f, 200.0f, 20.0f, 40.0f)
        val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))
        builder.getLayer().renderRectangle(aabb, options)
        val svg = builder.finish()

        assertEquals(
            """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <rect x="100.000" y="200.000" width="20.0000" height="40.0000" style="fill:none;stroke:green;stroke-width:10.0000"/>
</svg>""",
            svg.export()
        )
    }


    @Nested
    inner class CircleTest {

        @Test
        fun `Render a circle`() {
            val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))
            builder.getLayer().renderCircle(Point2d.fromMeters(110.0f, 220.0f), fromMillimeters(10000), options)

            testCircle(builder)
        }

        @Test
        fun `Render an aabb as a circle`() {
            val aabb = AABB.fromMeters(100.0f, 200.0f, 20.0f, 40.0f)
            val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))
            builder.getLayer().renderCircle(aabb, options)

            testCircle(builder)
        }

        private fun testCircle(builder: SvgBuilder) {
            val svg = builder.finish()

            assertEquals(
                """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <circle cx="110.000" cy="220.000" r="10.0000" style="fill:blue;stroke:red;stroke-width:5.0000"/>
</svg>""",
                svg.export()
            )
        }
    }

    @Test
    fun `Render an ellipse`() {
        val options = NoBorder(Green.toRender())
        val aabb = AABB.fromMeters(100.0f, 200.0f, 20.0f, 40.0f)
        val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))

        builder.getLayer().renderEllipse(aabb, options)

        assertEquals(
            """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <ellipse cx="110.000" cy="220.000" rx="10.0000" ry="20.0000" style="fill:green"/>
</svg>""",
            builder.finish().export()
        )
    }

    @Test
    fun `Render a line`() {
        val options = LineOptions(Black.toRender(), 0.5f)
        val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))

        builder.getLayer().renderLine(listOf(Point2d(1.2f, 3.4f), Point2d(10.0f, 20.0f)), options)

        assertEquals(
            """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <path d="M 1.2000 3.4000 L 10.0000 20.0000" style="fill:none;stroke:black;stroke-width:0.5000"/>
</svg>""",
            builder.finish().export()
        )
    }

    @Test
    fun `Render a polygon`() {
        val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))
        val corners = listOf(Point2d(1.2f, 3.4f), Point2d(10.0f, 20.0f), Point2d(30.0f, 40.0f))

        builder.getLayer().renderPolygon(Polygon2d(corners), options)

        assertEquals(
            """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <path d="M 1.2000 3.4000 L 10.0000 20.0000 L 30.0000 40.0000 Z" style="fill:blue;stroke:red;stroke-width:5.0000"/>
</svg>""",
            builder.finish().export()
        )
    }

    @Test
    fun `Render a string`() {
        val builder = SvgBuilder(Size2d.fromMeters(100.0f, 150.0f))

        builder.getLayer().renderString(
            "test",
            Point2d(1.0f, 2.0f),
            zero(),
            RenderStringOptions(Blue.toRender(), fromMeters(0.3f)),
        )

        assertEquals(
            """<svg viewBox="0 0 100.000 150.000" xmlns="http://www.w3.org/2000/svg">
  <text x="1.000" y="2.000" alignment-baseline="middle" style="fill:blue;font-size:0.300px" text-anchor="middle">test</text>
</svg>""",
            builder.finish().export()
        )
    }


}