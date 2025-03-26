package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FactorTest {
    @Test
    fun `Interpolate between 2 factors`() {
        val start = fromPercentage(1000)
        val end = fromPercentage(1400)

        testInterpolate(start, end, START, 1000)
        testInterpolate(start, end, CENTER, 1200)
        testInterpolate(start, end, END, 1400)
    }

    private fun testInterpolate(start: Factor, end: Factor, input: Factor, result: Int) {
        assertEquals(fromPercentage(result), start.interpolate(end, input))
    }
}