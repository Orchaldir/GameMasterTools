package at.orchaldir.gm.utils.map

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class MapSize2dTest {

    private val size = MapSize2d(3, 2)

    @Test
    fun `Get number of tiles in map`() {
        assertEquals(6, size.tiles())
    }

    @ParameterizedTest(name = "x={0} y={1}")
    @MethodSource("inside")
    fun `Test if inside for points inside`(x: Int, y: Int, index: Int) {
        assertTrue(size.isInside(x, y))
    }

    @ParameterizedTest(name = "x={0} y={1}")
    @MethodSource("outside")
    fun `Test if inside for points outside`(x: Int, y: Int) {
        assertFalse(size.isInside(x, y))
    }

    @ParameterizedTest(name = "x={0} y={1} -> {2}")
    @MethodSource("inside")
    fun `Get index for points inside`(x: Int, y: Int, index: Int) {
        assertEquals(index, size.toIndex(x, y))
    }

    @ParameterizedTest(name = "x={0} y={1}")
    @MethodSource("outside")
    fun `Get no index for points outside`(x: Int, y: Int) {
        assertNull(size.toIndex(x, y))
    }

    companion object {
        @JvmStatic
        fun inside() = listOf(
            Arguments.of(0, 0, 0),
            Arguments.of(1, 0, 1),
            Arguments.of(2, 0, 2),
            Arguments.of(0, 1, 3),
            Arguments.of(1, 1, 4),
            Arguments.of(2, 1, 5),
        )

        @JvmStatic
        fun outside() = listOf(
            Arguments.of(-1, 0),
            Arguments.of(0, -1),
            Arguments.of(-1, -1),
            Arguments.of(3, 0),
            Arguments.of(0, 2),
            Arguments.of(3, 2),
        )
    }
}