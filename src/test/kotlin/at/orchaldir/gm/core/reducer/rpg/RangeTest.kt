package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.UNKNOWN_STATISTIC_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.FixedHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.MusclePoweredHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.combat.Range
import at.orchaldir.gm.core.model.rpg.combat.StatisticBasedHalfAndMaxRange
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RangeTest {

    private val STATE = State(
        listOf(
            Storage(Statistic(STATISTIC_ID_0)),
        )
    )

    @Nested
    inner class FixedHalfAndMaxRangeTest {
        @Test
        fun `Half Range must be greater 0`() {
            val range = FixedHalfAndMaxRange(0, 10)

            assertInvalidRange(range, "Half range must be > 0!")
        }

        @Test
        fun `Max Range must be greater than the half range`() {
            val range = FixedHalfAndMaxRange(10, 10)

            assertInvalidRange(range, "Max range must be > half range!")
        }

        @Test
        fun `Test valid range`() {
            validateRange(STATE, FixedHalfAndMaxRange(10, 20))
        }
    }

    @Nested
    inner class MusclePoweredHalfAndMaxRangeTest {
        @Test
        fun `Half Range must be greater 0`() {
            val range = MusclePoweredHalfAndMaxRange(ZERO, ONE)

            assertInvalidRange(range, "Half range must be > 0%!")
        }

        @Test
        fun `Max Range must be greater than the half range`() {
            val range = MusclePoweredHalfAndMaxRange(ONE, ONE)

            assertInvalidRange(range, "Max range must be > half range!")
        }

        @Test
        fun `Test valid range`() {
            validateRange(STATE, MusclePoweredHalfAndMaxRange(ONE, DOUBLE))
        }
    }

    @Nested
    inner class StatisticBasedHalfAndMaxRangeTest {
        @Test
        fun `Unknown statistic`() {
            val range = StatisticBasedHalfAndMaxRange(UNKNOWN_STATISTIC_ID, ZERO, ONE)

            assertInvalidRange(range, "Requires unknown Statistic 99!")
        }

        @Test
        fun `Half Range must be greater 0`() {
            val range = StatisticBasedHalfAndMaxRange(STATISTIC_ID_0, ZERO, ONE)

            assertInvalidRange(range, "Half range must be > 0%!")
        }

        @Test
        fun `Max Range must be greater than the half range`() {
            val range = StatisticBasedHalfAndMaxRange(STATISTIC_ID_0, ONE, ONE)

            assertInvalidRange(range, "Max range must be > half range!")
        }

        @Test
        fun `Test valid range`() {
            validateRange(STATE, StatisticBasedHalfAndMaxRange(STATISTIC_ID_0, ONE, DOUBLE))
        }
    }

    private fun assertInvalidRange(range: Range, message: String) {
        assertIllegalArgument(message) { validateRange(STATE, range) }
    }

}