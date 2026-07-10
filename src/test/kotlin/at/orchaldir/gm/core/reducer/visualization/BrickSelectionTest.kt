package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.economy.material.Rock
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.AlternateRows
import at.orchaldir.gm.core.model.visualization.BrickSelection
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MIN = 2
private const val MAX = 4

class BrickSelectionTest {

    private val STATE = State(
        listOf(
            Storage(Material(MATERIAL_ID_0, properties = MaterialProperties(Rock()))),
        )
    )

    private val brick0 = RectangularShapeGrammar(Color.Blue)
    private val brick1 = RectangularShapeGrammar(Color.Green)
    private val invalidBrick = RectangularShapeGrammar(MadeFromStone(UNKNOWN_MATERIAL_ID))


    @Nested
    inner class AlternateRowsTest {

        @Test
        fun `Test the no rows`() {
            fail(AlternateRows(emptyList()), "test has too few rows!")
        }

        @Test
        fun `Test only 1 row`() {
            fail(AlternateRows(listOf(brick0)), "test has too few rows!")
        }

        @Test
        fun `Unknown material`() {
            fail(AlternateRows(listOf(brick0, invalidBrick)), "Requires unknown Material 99!")
        }

        @Test
        fun `Multiple different rows`() {
            success(AlternateRows(listOf(brick0, brick1)))
        }

    }

    fun fail(selection: BrickSelection, message: String) {
        assertIllegalArgument(message) { success(selection) }
    }

    fun success(selection: BrickSelection) {
        selection.validate(STATE, "test")
    }
}