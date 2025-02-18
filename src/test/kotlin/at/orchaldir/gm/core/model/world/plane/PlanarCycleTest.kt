package at.orchaldir.gm.core.model.world.plane

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlanarCycleTest {

    private val cycle = PlanarCycle(1, 3, 5, 11)

    @Test
    fun `Get length of cycle cycle`() {
        assertEquals(20, cycle.getLength())
    }

}