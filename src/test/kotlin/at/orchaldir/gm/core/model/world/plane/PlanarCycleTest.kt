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
            assertEquals(Waxing, cycle.getAlignment(0))
            assertEquals(Coterminous, cycle.getAlignment(1))
            assertEquals(Coterminous, cycle.getAlignment(2))
            assertEquals(Waning, cycle.getAlignment(3))
            assertEquals(Waning, cycle.getAlignment(4))
            assertEquals(Waning, cycle.getAlignment(5))
            assertEquals(Remote, cycle.getAlignment(6))
            assertEquals(Remote, cycle.getAlignment(7))
            assertEquals(Remote, cycle.getAlignment(8))
        }

    }

}