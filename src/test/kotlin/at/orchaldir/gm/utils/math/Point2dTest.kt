package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Point2dTest {

    @Test
    fun `Calculate the length`() {
        assertEquals(5.0f, Point2d(-3.0f, 4.0f).length())
    }

    @Test
    fun `Calculate the distance`() {
        assertEquals(5.0f, Point2d(2.0f, 3.0f).calculateDistance(Point2d(5.0f, 7.0f)))
    }

    @Test
    fun `Create with polar coordinates`() {
        val start = Point2d(2.0f, 4.0f)

        val result = start.createPolar(Distance(5000), Orientation.fromDegree(180.0f))

        assertEquals(-3.0f, result.x, 0.0001f)
        assertEquals(4.0f, result.y, 0.0001f)
    }
}