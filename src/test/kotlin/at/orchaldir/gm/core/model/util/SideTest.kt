package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.util.Side.Left
import at.orchaldir.gm.core.model.util.Side.Right
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SideTest {

    @Test
    fun `Flipping a side returns the other side`() {
        assertEquals(Left, Right.flip())
        assertEquals(Right, Left.flip())
    }

    @Test
    fun `Get a side from a pair`() {
        val pair = Pair(1, 2)

        assertEquals(1, Left.get(pair))
        assertEquals(2, Right.get(pair))
    }

}