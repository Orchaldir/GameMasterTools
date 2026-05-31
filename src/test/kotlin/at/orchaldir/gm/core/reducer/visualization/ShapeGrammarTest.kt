package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_BRANCHES
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_BRANCHES
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleBranching
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.MAX_BRICK_LENGTH
import at.orchaldir.gm.core.model.visualization.MAX_GRID_SIZE
import at.orchaldir.gm.core.model.visualization.MIN_BRICK_LENGTH
import at.orchaldir.gm.core.model.visualization.MIN_GRID_SIZE
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ShapeGrammarTest {

    private val STATE = State(
        listOf(
            Storage(Material(MATERIAL_ID_0, properties=MaterialProperties(Metal()))),
        )
    )

    @Nested
    inner class BrickPatternGrammarTest {

        @Test
        fun `Test invalid brick`() {
            val brick = RectangularShapeGrammar(MadeFromMetal(UNKNOWN_MATERIAL_ID))
            val grammar = BrickPatternGrammar(brick)

            fail(grammar, "Requires unknown Material 99!")
        }

        @Test
        fun `Test the grid size`() {
            assertInt(
                "test's size",
                MIN_GRID_SIZE,
                MAX_GRID_SIZE,
                { size, message ->
                    fail(BrickPatternGrammar(size = SquareGrid(size)), message)
                },
                { size ->
                    success(BrickPatternGrammar(size = SquareGrid(size)))
                },
            )
        }

        @Test
        fun `Test the brick length`() {
            assertInt(
                "test's brick length",
                MIN_BRICK_LENGTH,
                MAX_BRICK_LENGTH,
                { length, message ->
                    fail(BrickPatternGrammar(length = length), message)
                },
                { length ->
                    success(BrickPatternGrammar(length = length))
                },
            )
        }

    }

    @Nested
    inner class RectangularShapeGrammarTest {

        @Test
        fun `The material is invalid`() {
            val grammar = RectangularShapeGrammar(MadeFromMetal(UNKNOWN_MATERIAL_ID))

            fail(grammar, "Requires unknown Material 99!")
        }

        @Test
        fun `The grammar is valid`() {
            val grammar = RectangularShapeGrammar(MadeFromMetal(MATERIAL_ID_0))

            success(grammar)
        }
    }

    fun fail(grammar: ShapeGrammar, message: String) {
        assertIllegalArgument(message) { success(grammar) }
    }

    fun success(grammar: ShapeGrammar) {
        grammar.validate(STATE, "test")
    }
}