package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FactorTest {
    @Test
    fun `Interpolate between 2 factors`() {
        val start = Factor(10.0f)
        val end = Factor(14.0f)

        testInterpolate(start, end, 0.0f, 10.0f)
        testInterpolate(start, end, 0.5f, 12.0f)
        testInterpolate(start, end, 1.0f, 14.0f)
    }

    private fun testInterpolate(start: Factor, end: Factor, input: Float, result: Float) {
        assertEquals(Factor(result), start.interpolate(end, input))
    }
}