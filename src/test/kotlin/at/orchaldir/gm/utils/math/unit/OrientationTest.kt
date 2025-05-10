package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromRadians
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.PI

private const val DELTA = 0.001f

class OrientationTest {
    private val pi = PI.toFloat()

    @Test
    fun `Create degrees`() {
        val orientation = fromDegrees(180.0f)

        assertEquals(180.0f, orientation.toDegrees())
        assertEquals(pi, orientation.toRadians())
    }

    @Test
    fun `Create radians`() {
        val orientation = fromRadians(pi)

        assertEquals(180.0f, orientation.toDegrees())
        assertEquals(pi, orientation.toRadians())
    }

    @Test
    fun `Is zero`() {
        assertTrue(ZERO_ORIENTATION.isZero())
        assertFalse(fromDegrees(90.0f).isZero())
    }

    @Nested
    inner class NormalizeTest {

        @Test
        fun `Normalize 0`() {
            assertNormalize(0.0f, 0.0f)
        }

        @Test
        fun `Normalize 2 pi`() {
            assertNormalize(360.0f, 0.0f)
        }

        @Test
        fun `Normalize a negative value`() {
            assertNormalize(-90.0f, 270.0f)
        }

        @Test
        fun `Normalize a large negative value`() {
            assertNormalize(-450.0f, 270.0f)
        }

        @Test
        fun `Normalize a value in range`() {
            assertNormalize(90.0f, 90.0f)
        }

        private fun assertNormalize(input: Float, output: Float) {
            assertEquals(fromDegrees(output), fromDegrees(input).normalizeZeroToTwoPi())
        }

    }

    @Test
    fun `Test cos`() {
        testCos(0.0f, 1.0f)
        testCos(90.0f, 0.0f)
        testCos(180.0f, -1.0f)
        testCos(270.0f, 0.0f)
        testCos(360.0f, 1.0f)
    }

    @Test
    fun `Test sin`() {
        testSin(0.0f, 0.0f)
        testSin(90.0f, 1.0f)
        testSin(180.0f, 0.0f)
        testSin(270.0f, -1.0f)
        testSin(360.0f, 0.0f)
    }

    private fun testCos(degrees: Float, result: Float) {
        assertEquals(result, fromDegrees(degrees).cos(), DELTA)
    }

    private fun testSin(degrees: Float, result: Float) {
        assertEquals(result, fromDegrees(degrees).sin(), DELTA)
    }

}