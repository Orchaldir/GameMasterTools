package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.utils.math.RangeInt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatisticLimitTest {

    @Nested
    inner class GetRelativeRangeTest {

        @Nested
        inner class AbsoluteStatisticLimitTest {
            val limit = AbsoluteStatisticLimit(RangeInt(1, 20))

            @Test
            fun `Test with the base inside the absolute limit`() {
                assertEquals(RangeInt(-9, 10), limit.getRelativeRange(10, 100))
                assertEquals(RangeInt(-10, 9), limit.getRelativeRange(11, 100))
            }

            @Test
            fun `Test with the base above the absolute limit`() {
                assertEquals(RangeInt(-21, -2), limit.getRelativeRange(22, 100))
            }

            @Test
            fun `Test with the base below the absolute limit`() {
                assertEquals(RangeInt(3, 22), limit.getRelativeRange(-2, 100))
            }
        }

        @Test
        fun `Test a relative limit`() {
            val range = RangeInt(-2, 4)
            val limit = RelativeStatisticLimit(range)

            assertEquals(range, limit.getRelativeRange(1, 100))
            assertEquals(range, limit.getRelativeRange(11, 100))
        }

        @Nested
        inner class RelativeLowerAndAbsoluteUpperLimitTest {
            val limit = RelativeLowerAndAbsoluteUpperLimit(-4, 20)

            @Test
            fun `Test with the base below the absolute upper limit`() {
                assertEquals(RangeInt(-4, 10), limit.getRelativeRange(10, 100))
                assertEquals(RangeInt(-4, 2), limit.getRelativeRange(18, 100))
            }

            @Test
            fun `Test with the base above the absolute upper limit`() {
                assertEquals(RangeInt(-6, -2), limit.getRelativeRange(22, 100))
                assertEquals(RangeInt(-16, -12), limit.getRelativeRange(32, 100))
            }
        }
    }

}