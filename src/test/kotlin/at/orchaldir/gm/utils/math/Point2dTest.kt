package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Orientation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Point2dTest {

    @Test
    fun `Calculate the length`() {
        assertEquals(fromMeters(5.0f), Point2d.fromMeters(-3.0f, 4.0f).length())
    }

    @Test
    fun `Calculate the distance`() {
        val a = Point2d.fromMeters(2.0f, 3.0f)
        val b = Point2d.fromMeters(5.0f, 7.0f)

        assertEquals(fromMeters(5.0f), a.calculateDistance(b))
    }

    @Test
    fun `Create with polar coordinates`() {
        val start = Point2d.fromMeters(2.0f, 4.0f)

        val result = start.createPolar(fromMillimeters(5000), Orientation.fromDegrees(180))

        assertEquals(-3.0f, result.x.toMeters(), 0.0001f)
        assertEquals(4.0f, result.y.toMeters(), 0.0001f)
    }
}