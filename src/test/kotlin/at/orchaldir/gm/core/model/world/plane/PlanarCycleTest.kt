package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlanarCycleTest {

    private val cycle = PlanarCycle(1, 2, 3, 4)

    @Nested
    inner class ConstructorTest {

        @Test
        fun `Invalid waxing`() {
            assertIllegalArgument("Waxing must be greater than 0!") { PlanarCycle(0, 1, 1, 1) }
        }

        @Test
        fun `Invalid coterminous`() {
            assertIllegalArgument("Coterminous must be greater than 0!") { PlanarCycle(1, 0, 1, 1) }
        }

        @Test
        fun `Invalid waning`() {
            assertIllegalArgument("Waning must be greater than 0!") { PlanarCycle(1, 1, 0, 1) }
        }

        @Test
        fun `Invalid remote`() {
            assertIllegalArgument("Remote must be greater than 0!") { PlanarCycle(1, 1, 1, 0) }
        }
    }

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
        fun `First Negative Cycle`() {
            assertCycle(-10)
        }

        @Test
        fun `Second Negative Cycle`() {
            assertCycle(-20)
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