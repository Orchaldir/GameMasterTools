package at.orchaldir.gm.utils.math

import at.orchaldir.gm.assertPoints
import at.orchaldir.gm.utils.math.Point2d.Companion.fromMeters
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubdivisionTest {

    @Nested
    inner class LineTest {

        @Test
        fun `Subdividing an empty list returns the input`() {
            val noPoints = emptyList<Point2d>()

            assertPoints(noPoints, subdivideLine(noPoints, 1))
        }

        @Test
        fun `Subdividing a single point returns the input`() {
            val point = listOf(Point2d())

            assertPoints(point, subdivideLine(point, 1))
        }

        @Test
        fun `Subdividing two points returns the input`() {
            val twoPoints = listOf(Point2d(), fromMeters(1.0f, 2.0f))

            assertPoints(twoPoints, subdivideLine(twoPoints, 1))
        }

        @Test
        fun `Subdivide a list of points`() {
            val input = listOf(Point2d(), fromMeters(3.0f, 0.0f), fromMeters(3.0f, 3.0f))
            val output = listOf(
                Point2d(),
                fromMeters(1.0f, 0.0f),
                fromMeters(2.0f, 0.0f),
                fromMeters(3.0f, 1.0f),
                fromMeters(3.0f, 2.0f),
                fromMeters(3.0f, 3.0f),
            )

            assertPoints(output, subdivideLine(input, 1))
        }
    }

    @Nested
    inner class PolygonTest {

        @Test
        fun `Subdividing an empty list returns the input`() {
            val noPoints = emptyList<Point2d>()

            test(noPoints, noPoints)
        }

        @Test
        fun `Subdividing a single point returns the input`() {
            val point = listOf(Point2d())

            test(point, point)
        }

        @Test
        fun `Subdividing two points returns the input`() {
            val twoPoints = listOf(Point2d(), fromMeters(1.0f, 2.0f))

            test(twoPoints, twoPoints)
        }

        @Test
        fun `Subdivide a list of points`() {
            val input = listOf(Point2d(), fromMeters(3.0f, 0.0f), fromMeters(3.0f, 3.0f))
            val output = listOf(
                fromMeters(1.5f, 0.0f),
                fromMeters(3.0f, 0.0f),
                fromMeters(3.0f, 1.5f),
                fromMeters(3.0f, 3.0f),
                fromMeters(1.5f, 1.5f),
                Point2d(),
            )

            test(input, output)
        }

        private fun test(input: List<Point2d>, output: List<Point2d>) {
            assertPoints(output, subdividePolygon(input, 1, ::halfSegment))
        }
    }

}