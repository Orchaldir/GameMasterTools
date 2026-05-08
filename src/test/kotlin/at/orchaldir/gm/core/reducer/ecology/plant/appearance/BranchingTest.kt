package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertFactor
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BranchingTest {

    @Nested
    inner class SimpleBranchingTest
    {
        @Test
        fun `Test the max branches`() {
            assertInt(
                "test's max branches",
                MIN_BRANCHES,
                MAX_BRANCHES,
                { branches, message ->
                    fail(SimpleBranching(branches), message)
                },
                { branches ->
                    success(SimpleBranching(branches))
                },
            )
        }

        @Test
        fun `Test the base factor`() {
            assertFactor(
                "test's branching base",
                MIN_BRANCHING_BASE,
                MAX_BRANCHING_BASE,
                { base, message ->
                    fail(SimpleBranching(base = base), message)
                },
                { base ->
                    success(SimpleBranching(base = base))
                },
            )
        }
    }

    fun success(branching: Branching) {
        val stem = Stem(MIN_SEGMENTS, branching = branching)

        stem.validate("test")
    }

    fun fail(branching: Branching, message: String) {
        val stem = Stem(MIN_SEGMENTS, branching = branching)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}