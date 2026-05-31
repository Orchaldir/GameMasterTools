package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
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