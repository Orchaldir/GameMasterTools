package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Orientation.Companion.fromDegree
import at.orchaldir.gm.utils.math.Orientation.Companion.fromRadians
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.PI

private const val DELTA = 0.001f

class OrientationTest {
    private val pi = PI.toFloat()

    @Test
    fun `Create degrees`() {
        val orientation = Orientation.fromDegree(180.0f)

        assertEquals(180.0f, orientation.toDegree())
        assertEquals(pi, orientation.toRadians())
    }

    @Test
    fun `Create radians`() {
        val orientation = fromRadians(pi)

        assertEquals(180.0f, orientation.toDegree())
        assertEquals(pi, orientation.toRadians())
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
            assertEquals(fromDegree(output), fromDegree(input).normalizeZeroToTwoPi())
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
        assertEquals(result, Orientation.fromDegree(degrees).cos(), DELTA)
    }

    private fun testSin(degrees: Float, result: Float) {
        assertEquals(result, Orientation.fromDegree(degrees).sin(), DELTA)
    }

}