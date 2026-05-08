package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StemTest {

    @Nested
    inner class SegmentsTest {

        @Test
        fun `Cannot use too few segments`() {
            fail(Stem(MIN_SEGMENTS - 1), "The test's number of segments is too small!")
        }

        @Test
        fun `Cannot use too many segments`() {
            fail(Stem(MAX_SEGMENTS + 1), "The test's number of segments is too large!")
        }
    }

    fun fail(stem: Stem, message: String) {
        assertIllegalArgument(message) { stem.validate("test") }
    }
}