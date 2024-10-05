package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AabbTest {

    private val aabb = AABB(2.0f, 3.0f, 30.0f, 60.0f)
    private val center = Point2d(17.0f, 33.0f)
    private val size = Size2d(30.0f, 60.0f)

    @Test
    fun `Create with size`() {
        assertEquals(AABB(Point2d(), size), AABB(size))
    }

    @Test
    fun `Create with center & size`() {
        assertEquals(aabb, AABB.fromCenter(center, size))
    }

    @Test
    fun `Create with center & radii`() {
        assertEquals(aabb, AABB.fromRadii(center, Distance(15.0f), Distance(30.0f)))
    }

    @Test
    fun `Calculate center`() {
        assertEquals(center, aabb.getCenter())
    }

    @Test
    fun `Calculate inner radius`() {
        assertInnerRadius(8.0f, 6.0f)
        assertInnerRadius(6.0f, 8.0f)
    }

    @Test
    fun `Get point inside AABB`() {
        val point = aabb.getPoint(Factor(0.5f), Factor(0.25f))

        assertEquals(Point2d(17.0f, 18.0f), point)
    }

    @Test
    fun `Get mirrored points inside AABB`() {
        val (left, right) = aabb.getMirroredPoints(Factor(0.5f), Factor(0.25f))

        assertEquals(Point2d(9.5f, 18.0f), left)
        assertEquals(Point2d(24.5f, 18.0f), right)
    }

    @Test
    fun `Shrink by a distance`() {
        assertEquals(AABB(3.0f, 4.0f, 28.0f, 58.0f), aabb.shrink(Distance(1.0f)))
    }

    @Test
    fun `Shrink by a factor`() {
        assertEquals(AABB(9.5f, 18.0f, 15.0f, 30.0f), aabb.shrink(Factor(0.5f)))
    }

    @Test
    fun `Shrink by another factor`() {
        assertEquals(AABB(3.5f, 6.0f, 27.0f, 54.0f), aabb.shrink(Factor(0.1f)))
    }

    @Test
    fun `Mirror a polygon`() {
        val polygon = Polygon2d(List(3) { Point2d(9.0f, 18.0f) })
        val mirrored = Polygon2d(List(3) { Point2d(25.0f, 18.0f) })

        assertEquals(mirrored, aabb.mirror(polygon))
    }

    @Test
    fun `Move an AABB`() {
        assertEquals(AABB(Point2d(12.0f, 23.0f), size), aabb + Point2d(10.0f, 20.0f))
    }

    private fun assertInnerRadius(width: Float, height: Float) {
        assertEquals(Distance(3.0f), AABB(2.0f, 3.0f, width, height).getInnerRadius())
    }

}