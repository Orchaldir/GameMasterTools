package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.utils.math.RangeInt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatisticLimitTest {

    @Nested
    inner class GetRelativeRangeTest {

        @Test
        fun `Test with the base inside the absolute limit`() {
            val limit = AbsoluteStatisticLimit(RangeInt(1, 20))

            assertEquals(RangeInt(-9, 10), limit.getRelativeRange(10, 100))
            assertEquals(RangeInt(-10, 9), limit.getRelativeRange(11, 100))
        }

        @Test
        fun `Test with the base above the absolute limit`() {
            val limit = AbsoluteStatisticLimit(RangeInt(1, 20))

            assertEquals(RangeInt(-21, -2), limit.getRelativeRange(22, 100))
        }

        @Test
        fun `Test with the base below the absolute limit`() {
            val limit = AbsoluteStatisticLimit(RangeInt(1, 20))

            assertEquals(RangeInt(3, 22), limit.getRelativeRange(-2, 100))
        }

        @Test
        fun `Test a relative limit`() {
            val range = RangeInt(-2, 4)
            val limit = RelativeStatisticLimit(range)

            assertEquals(range, limit.getRelativeRange(1, 100))
            assertEquals(range, limit.getRelativeRange(11, 100))
        }
    }

}