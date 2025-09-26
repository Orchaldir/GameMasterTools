package at.orchaldir.gm.core.model.character.statistic

import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.UNKNOWN_STATISTIC_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatblockTest {

    val attribute = Statistic(STATISTIC_ID_0, data = Attribute(FixedNumber(10)))
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

    }

}