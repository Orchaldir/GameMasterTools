package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Size2dTest {

    @Test
    fun `Create square`() {
        val distance = fromMeters(5)
        val result = Size2d(distance, distance)

        assertEquals(result, Size2d.square(distance))
    }

    @Test
    fun `Scale size`() {
        val input = Size2d(fromMeters(10), fromMeters(20))
        val result = Size2d(fromMeters(15), fromMeters(60))

        assertEquals(result, input.scale(fromPercentage(150), fromPercentage(300)))
    }

}