package at.orchaldir.gm.core.reducer.visualization

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.DOMAIN_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.SPELL_ID_0
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertFactor
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.assertVariance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.visualization.Grammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GrammarTest {

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

    fun fail(grammar: Grammar, message: String) {
        assertIllegalArgument(message) { success(grammar) }
    }

    fun success(grammar: Grammar) {
        grammar.validate(STATE, "test")
    }
}