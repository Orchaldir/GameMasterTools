package at.orchaldir.gm.utils.math

import at.orchaldir.gm.assertIllegalArgument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SegmentWeightCalculatorTest {

    @Nested
    inner class ConstantWeightTest {

        private val calculator = ConstantWeight(4)

        @Nested
        inner class CalculateTest {

            @Test
            fun `Test valid indices`() {
                assertEquals(0.25f, calculator.calculate(0))
                assertEquals(0.25f, calculator.calculate(1))
                assertEquals(0.25f, calculator.calculate(2))
                assertEquals(0.25f, calculator.calculate(3))
            }

            @Test
            fun `Negative index`() {
                assertIllegalArgument("Index -1 is invalid!") { calculator.calculate(-1) }
            }

            @Test
            fun `Index too high`() {
                assertIllegalArgument("Index 4 is invalid!") { calculator.calculate(4) }
            }

        }

        @Nested
        inner class SumUntilTest {

            @Test
            fun `Test valid indices`() {
                assertEquals(0.0f, calculator.sumUntil(0))
                assertEquals(0.25f, calculator.sumUntil(1))
                assertEquals(0.5f, calculator.sumUntil(2))
                assertEquals(0.75f, calculator.sumUntil(3))
            }

            @Test
            fun `Negative index`() {
                assertIllegalArgument("Index -1 is invalid!") { calculator.sumUntil(-1) }
            }

            @Test
            fun `Index too high`() {
                assertIllegalArgument("Index 4 is invalid!") { calculator.sumUntil(4) }
            }

        }

    }

    @Nested
    inner class LinearDecreasingWeightTest {

        private val calculator = LinearDecreasingWeight(3)

        @Nested
        inner class CalculateTest {

            @Test
            fun `Test valid indices`() {
                assertEquals(4.0f / 9.0f, calculator.calculate(0))
                assertEquals(3.0f / 9.0f, calculator.calculate(1))
                assertEquals(2.0f / 9.0f, calculator.calculate(2))
            }

            @Test
            fun `Negative index`() {
                assertIllegalArgument("Index -1 is invalid!") { calculator.calculate(-1) }
            }

            @Test
            fun `Index too high`() {
                assertIllegalArgument("Index 3 is invalid!") { calculator.calculate(3) }
            }

        }

        @Nested
        inner class SumUntilTest {

            @Test
            fun `Test valid indices`() {
                assertEquals(0.0f / 9.0f, calculator.sumUntil(0))
                assertEquals(4.0f / 9.0f, calculator.sumUntil(1))
                assertEquals(7.0f / 9.0f, calculator.sumUntil(2))
            }

            @Test
            fun `Negative index`() {
                assertIllegalArgument("Index -1 is invalid!") { calculator.sumUntil(-1) }
            }

            @Test
            fun `Index too high`() {
                assertIllegalArgument("Index 4 is invalid!") { calculator.sumUntil(4) }
            }

        }

    }

}