package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlanarCycleTest {

    private val cycle = PlanarCycle(1, 2, 3, 4)

    @Test
    fun `Get length of cycle cycle`() {
        assertEquals(10, cycle.getLength())
    }

    @Nested
    inner class GetAlignmentTest {

        @Test
        fun `First Cycle`() {
            assertCycle(0)
        }

        @Test
        fun `Second Cycle`() {
            assertCycle(10)
        }

        @Test
        fun `Negative Cycle`() {
            assertCycle(-10)
        }

        private fun assertCycle(start: Int) {
            assertEquals(Waxing, cycle.getAlignment(start))
            assertEquals(Coterminous, cycle.getAlignment(start + 1))
            assertEquals(Coterminous, cycle.getAlignment(start + 2))
            assertEquals(Waning, cycle.getAlignment(start + 3))
            assertEquals(Waning, cycle.getAlignment(start + 4))
            assertEquals(Waning, cycle.getAlignment(start + 5))
            assertEquals(Remote, cycle.getAlignment(start + 6))
            assertEquals(Remote, cycle.getAlignment(start + 7))
            assertEquals(Remote, cycle.getAlignment(start + 8))
            assertEquals(Remote, cycle.getAlignment(start + 9))
        }

    }

}