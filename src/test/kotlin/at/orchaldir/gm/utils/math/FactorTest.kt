package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FactorTest {

    @Test
    fun `Test unary Minus`() {
        assertEquals(fromPercentage(-10), -fromPercentage(10))
        assertEquals(fromPercentage(10), -fromPercentage(-10))
    }

    @Test
    fun `Test plus`() {
        assertEquals(fromPercentage(15), fromPercentage(10) + fromPercentage(5))
    }

    @Test
    fun `Test minus`() {
        assertEquals(fromPercentage(73), fromPercentage(100) - fromPercentage(27))
    }

    @Test
    fun `Test times with float`() {
        assertEquals(fromPercentage(250), fromPercentage(100) * 2.5f)
    }

    @Test
    fun `Test times with factor`() {
        assertEquals(fromPercentage(100), fromPercentage(200) * fromPercentage(50))
    }

    @Test
    fun `Test div with int`() {
        assertEquals(fromPercentage(50), fromPercentage(100) / 2)
    }

    @Test
    fun `Test div with float`() {
        assertEquals(fromPercentage(50), fromPercentage(100) / 2.0f)
    }

    @Test
    fun `Test div with factor`() {
        assertEquals(fromPercentage(400), fromPercentage(200) / fromPercentage(50))
    }

    @Test
    fun `Factor to string`() {
        assertEquals("123%", fromPercentage(123).toString())
        assertEquals("12%", fromPercentage(12).toString())
        assertEquals("80%", fromPercentage(80).toString())
        assertEquals("1%", fromPercentage(1).toString())
        assertEquals("12.3%", fromPermille(123).toString())
        assertEquals("12.3%", fromNumber(0.1234f).toString())
    }

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