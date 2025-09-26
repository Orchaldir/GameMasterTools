package at.orchaldir.gm.core.model.character.statistic

import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.STATISTIC_ID_1
import at.orchaldir.gm.UNKNOWN_STATISTIC_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatblockTest {

    private val attribute = Statistic(STATISTIC_ID_0, data = Attribute(FixedNumber(10)))
    private val state = State(
        listOf(
            Storage(attribute),
        )
    )

    @Nested
    inner class ResolveTest {

        @Test
        fun `Resolve unknown statistic`() {
            val statblock = Statblock()

            assertIllegalArgument("Requires unknown Statistic 99!") { statblock.resolve(state, UNKNOWN_STATISTIC_ID) }
        }

        @Test
        fun `Resolve contained attribute`() {
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 2))

            assertEquals(12, statblock.resolve(state, STATISTIC_ID_0))
        }

        @Test
        fun `Resolve default value of attribute`() {
            val statblock = Statblock()

            assertEquals(10, statblock.resolve(state, STATISTIC_ID_0))
        }

        @Test
        fun `Resolve derived attribute`() {
            val derived = Statistic(STATISTIC_ID_1, data = Attribute(BasedOnStatistic(STATISTIC_ID_0, -1)))
            val newState = state.updateStorage(Storage(listOf(attribute, derived)))
            val statblock = Statblock(mapOf(STATISTIC_ID_0 to 5, STATISTIC_ID_1 to 4))

            assertEquals(18, statblock.resolve(newState, STATISTIC_ID_1))
        }

        @Test
        fun `Resolve default value derived attribute`() {
            val derived = Statistic(STATISTIC_ID_1, data = Attribute(BasedOnStatistic(STATISTIC_ID_0, -1)))
            val newState = state.updateStorage(Storage(listOf(attribute, derived)))
            val statblock = Statblock()

            assertEquals(9, statblock.resolve(newState, STATISTIC_ID_1))
        }

    }

}