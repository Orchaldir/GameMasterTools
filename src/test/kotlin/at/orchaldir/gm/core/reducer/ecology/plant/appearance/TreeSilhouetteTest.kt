package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertFactor
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.assertVariance
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TreeSilhouetteTest {

    @Nested
    inner class SimpleTreeSilhouetteTest {

        @Test
        fun `Test the base factor`() {
            assertFactor(
                "silhouette's base",
                MIN_BRANCHING_BASE,
                MAX_BRANCHING_BASE,
                { base, message ->
                    fail(SimpleTreeSilhouette(base = base), message)
                },
                { base ->
                    success(SimpleTreeSilhouette(base = base))
                },
            )
        }
    }

    fun success(silhouette: TreeSilhouette) {
        silhouette.validate()
    }

    fun fail(silhouette: TreeSilhouette, message: String) {
        assertIllegalArgument(message) { silhouette.validate() }
    }
}