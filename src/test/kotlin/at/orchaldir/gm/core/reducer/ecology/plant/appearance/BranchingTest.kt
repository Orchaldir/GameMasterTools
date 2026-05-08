package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BranchingTest {

    @Nested
    inner class SimpleBranchingTest
    {

        @Test
        fun `Cannot use a too few branches`() {
            fail(
                SimpleBranching(MIN_BRANCHES - 1),
                "The test's max branches is too small!",
            )
        }
    }


    fun fail(branching: Branching, message: String) {
        val stem = Stem(MIN_SEGMENTS, branching = branching)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}