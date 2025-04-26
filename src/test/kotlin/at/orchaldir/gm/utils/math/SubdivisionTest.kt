package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SubdivisionTest {

    @Test
    fun `Subdividing an empty list returns the input`() {
        val noPoints = emptyList<Point2d>()

        assertEquals(noPoints, subdividePoints(noPoints, 1))
    }

    @Test
    fun `Subdividing a single point returns the input`() {
        val point = listOf(Point2d())

        assertEquals(point, subdividePoints(point, 1))
    }

    @Test
    fun `Subdividing two points returns the input`() {
        val twoPoints = listOf(Point2d(), Point2d(1.0f, 2.0f))

        assertEquals(twoPoints, subdividePoints(twoPoints, 1))
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

        assertEquals(output, subdividePoints(input, 1))
    }
}