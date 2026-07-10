package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Rock
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
        fun `A row has an invalid brick`() {
            fail(AlternateRows(listOf(brick0, invalidBrick)), "Requires unknown Material 99!")
        }

        @Test
        fun `A valid selection`() {
            success(AlternateRows(listOf(brick0, brick1)))
        }

    }

    @Nested
    inner class HorizontalAndVerticalBricksTest {

        @Test
        fun `Horizontal has an invalid brick`() {
            fail(HorizontalAndVerticalBricks(invalidBrick, brick1), "Requires unknown Material 99!")
        }

        @Test
        fun `Vertical has an invalid brick`() {
            fail(HorizontalAndVerticalBricks(brick0, invalidBrick), "Requires unknown Material 99!")
        }

        @Test
        fun `A valid selection`() {
            success(HorizontalAndVerticalBricks(brick0, brick1))
        }

    }

    @Nested
    inner class UniformBricksTest {

        @Test
        fun `Test with an invalid brick`() {
            fail(UniformBricks(invalidBrick), "Requires unknown Material 99!")
        }

        @Test
        fun `A valid selection`() {
            success(UniformBricks(brick0))
        }

    }

    fun fail(selection: BrickSelection, message: String) {
        assertIllegalArgument(message) { success(selection) }
    }

    fun success(selection: BrickSelection) {
        selection.validate(STATE, "test")
    }
}