package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RangeTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
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

    private fun assertInvalidRange(range: Range, message: String) {
        assertIllegalArgument(message) { validateRange(STATE, range) }
    }

}