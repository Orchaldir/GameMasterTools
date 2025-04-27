package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubdivisionTest {

    @Nested
    inner class LineTest {

        @Test
        fun `Subdividing an empty list returns the input`() {
            val noPoints = emptyList<Point2d>()

            assertEquals(noPoints, subdivideLine(noPoints, 1))
        }

        @Test
        fun `Subdividing a single point returns the input`() {
            val point = listOf(Point2d())

            assertEquals(point, subdivideLine(point, 1))
        }

        @Test
        fun `Subdividing two points returns the input`() {
            val twoPoints = listOf(Point2d(), Point2d(1.0f, 2.0f))

            assertEquals(twoPoints, subdivideLine(twoPoints, 1))
        }

        @Test
        fun `Subdivide a list of points`() {
            val input = listOf(Point2d(), Point2d(3.0f, 0.0f), Point2d(3.0f, 3.0f))
            val output = listOf(
                Point2d(),
                Point2d(1.0f, 0.0f),
                Point2d(2.0f, 0.0f),
                Point2d(3.0f, 1.0f),
                Point2d(3.0f, 2.0f),
                Point2d(3.0f, 3.0f),
            )

            assertEquals(output, subdivideLine(input, 1))
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
            val twoPoints = listOf(Point2d(), Point2d(1.0f, 2.0f))

            test(twoPoints, twoPoints)
        }

        @Test
        fun `Subdivide a list of points`() {
            val input = listOf(Point2d(), Point2d(3.0f, 0.0f), Point2d(3.0f, 3.0f))
            val output = listOf(
                Point2d(1.5f, 0.0f),
                Point2d(3.0f, 0.0f),
                Point2d(3.0f, 1.5f),
                Point2d(3.0f, 3.0f),
                Point2d(1.5f, 1.5f),
                Point2d(),
            )

            test(input, output)
        }

        private fun test(input: List<Point2d>, output: List<Point2d>) {
            assertEquals(output, subdividePolygon(input, 1, ::halfSegment))
        }
    }

}