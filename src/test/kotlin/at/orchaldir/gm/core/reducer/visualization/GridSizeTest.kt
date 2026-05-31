package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.MAX_BRICK_LENGTH
import at.orchaldir.gm.core.model.visualization.MIN_BRICK_LENGTH
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MIN = 2
private const val MAX = 4

class GridSizeTest {

    @Nested
    inner class SquareGridTest {

        @Test
        fun `Test the brick length`() {
            assertInt(
                "test's size",
                MIN,
                MAX,
                { length, message ->
                    fail(SquareGrid(length), message)
                },
                { length ->
                    success(SquareGrid(length))
                },
            )
        }

    }

    fun fail(size: GridSize, message: String) {
        assertIllegalArgument(message) { success(size) }
    }

    fun success(size: GridSize) {
        size.validate("test", MIN, MAX)
    }
}