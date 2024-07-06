package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FactorTest {
    @Test
    fun `Interpolate between 2 factors`() {
        val start = Factor(10.0f)
        val end = Factor(14.0f)

        testInterpolate(start, end, START, 10.0f)
        testInterpolate(start, end, CENTER, 12.0f)
        testInterpolate(start, end, END, 14.0f)
    }

    private fun testInterpolate(start: Factor, end: Factor, input: Factor, result: Float) {
        assertEquals(Factor(result), start.interpolate(end, input))
    }
}