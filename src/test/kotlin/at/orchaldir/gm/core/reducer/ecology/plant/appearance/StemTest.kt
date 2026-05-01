package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.CurvedStem
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_CURVE_CENTER
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_CURVE_CENTER
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemShape
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StemTest {

    private val state = State(
        listOf(
            Storage(Material(MATERIAL_ID_0)),
        )
    )

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

    @Nested
    inner class ShapeTest {

        @Test
        fun `Cannot use a too small curve's center`() {
            fail(
                CurvedStem(MIN_CURVE_CENTER - fromDegrees(1)),
                "The test's curve's center is too small!",
            )
        }

        @Test
        fun `Cannot use a too large curve's center`() {
            fail(
                CurvedStem(MAX_CURVE_CENTER + fromDegrees(1)),
                "The test's curve's center is too large!",
            )
        }

        fun fail(shape: StemShape, message: String) {
            fail(Stem(MIN_SEGMENTS, shape), message)
        }
    }

    fun fail(stem: Stem, message: String) {
        assertIllegalArgument(message) { stem.validate("test") }
    }
}