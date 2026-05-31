package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.utils.map.MapSize2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MIN = 2
private const val MAX = 4

class GridSizeTest {

    @Test
    fun `Test the SquareGridbrick length`() {
        test("test's size", ::SquareGrid)
    }

    @Nested
    inner class RowsAndColumnsTest {

        @Test
        fun `Test the width`() {
            test("test's width") { size ->
                RowsAndColumns(MapSize2d(size, MIN))
            }
        }

        @Test
        fun `Test the height`() {
            test("test's height") { size ->
                RowsAndColumns(MapSize2d(MIN, size))
            }
        }

    }

    private fun test(text: String, create: (Int) -> GridSize) {
        assertInt(
            text,
            MIN,
            MAX,
            { size, message ->
                fail(create(size), message)
            },
            { size ->
                success(create(size))
            },
        )
    }

    fun fail(size: GridSize, message: String) {
        assertIllegalArgument(message) { success(size) }
    }

    fun success(size: GridSize) {
        size.validate("test", MIN, MAX)
    }
}