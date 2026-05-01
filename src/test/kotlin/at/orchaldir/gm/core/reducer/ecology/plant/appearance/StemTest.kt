package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.BaseSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.ConstantStemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.CurvedStem
import at.orchaldir.gm.core.model.ecology.plant.appearance.DEFAULT_RELATIVE_TO_LENGTH
import at.orchaldir.gm.core.model.ecology.plant.appearance.LinearStemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_CURVE_CENTER
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_CURVE_OFFSET
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_RELATIVE_TO_LENGTH
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SPLITTING_ANGLE
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SPLITTING_OFFSET
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SPLITTING_PROBABILITY
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_CURVE_CENTER
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_CURVE_OFFSET
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_RELATIVE_TO_LENGTH
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SPLITTING_ANGLE
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SPLITTING_OFFSET
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SPLITTING_PROBABILITY
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemShape
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemThickness
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

        @Test
        fun `Cannot use a too small curve's offset`() {
            val angle = Variance(ZERO_ORIENTATION, MIN_CURVE_OFFSET - fromDegrees(1))

            fail(
                CurvedStem(angle),
                "The test's curve's offset is too small!",
            )
        }

        @Test
        fun `Cannot use a too large curve's offset`() {
            val angle = Variance(ZERO_ORIENTATION, MAX_CURVE_OFFSET + fromDegrees(1))

            fail(
                CurvedStem(angle),
                "The test's curve's offset is too large!",
            )
        }

        fun fail(shape: StemShape, message: String) {
            fail(Stem(MIN_SEGMENTS, shape), message)
        }
    }

    @Nested
    inner class ThicknessTest {

        @Nested
        inner class ConstantThicknessTest {

            @Test
            fun `Cannot use a too small thickness factor`() {
                fail(
                    ConstantStemThickness(MIN_RELATIVE_TO_LENGTH - ONE_TENTH_PERCENT),
                    "The test's start thickness factor is too small!",
                )
            }

            @Test
            fun `Cannot use a too large thickness factor`() {
                fail(
                    ConstantStemThickness(MAX_RELATIVE_TO_LENGTH + ONE_TENTH_PERCENT),
                    "The test's start thickness factor is too large!",
                )
            }
        }

        @Nested
        inner class LinearThicknessTest {

            @Test
            fun `Cannot use a too small start thickness factor`() {
                fail(
                    LinearStemThickness(MIN_RELATIVE_TO_LENGTH - ONE_TENTH_PERCENT),
                    "The test's start thickness factor is too small!",
                )
            }

            @Test
            fun `Cannot use a too large start thickness factor`() {
                fail(
                    LinearStemThickness(MAX_RELATIVE_TO_LENGTH + ONE_TENTH_PERCENT),
                    "The test's start thickness factor is too large!",
                )
            }

            @Test
            fun `Cannot use a too small end thickness factor`() {
                fail(
                    LinearStemThickness(DEFAULT_RELATIVE_TO_LENGTH, -ONE_TENTH_PERCENT),
                    "The test's end thickness factor is too small!",
                )
            }

            @Test
            fun `Cannot use a too large end thickness factor`() {
                fail(
                    LinearStemThickness(DEFAULT_RELATIVE_TO_LENGTH, FULL + ONE_TENTH_PERCENT),
                    "The test's end thickness factor is too large!",
                )
            }
        }

        fun fail(thickness: StemThickness, message: String) {
            fail(Stem(MIN_SEGMENTS, thickness = thickness), message)
        }
    }

    @Nested
    inner class SplittingTest {

        @Nested
        inner class BaseTest {

            @Test
            fun `Cannot use a too small thickness factor`() {
                fail(
                    BaseSplitting(MIN_SPLITTING_PROBABILITY - ONE_TENTH_PERCENT),
                    "The test's splitting probability factor is too small!",
                )
            }

            @Test
            fun `Cannot use a too large thickness factor`() {
                fail(
                    BaseSplitting(MAX_SPLITTING_PROBABILITY + ONE_TENTH_PERCENT),
                    "The test's splitting probability factor is too large!",
                )
            }

            @Test
            fun `Cannot use a too small angle's center`() {
                fail(
                    Variance(MIN_SPLITTING_ANGLE - fromDegrees(1)),
                    "The test's splitting angle's center is too small!",
                )
            }

            @Test
            fun `Cannot use a too large angle's center`() {
                fail(
                    Variance(MAX_SPLITTING_ANGLE + fromDegrees(1)),
                    "The test's splitting angle's center is too large!",
                )
            }

            @Test
            fun `Cannot use a too small angle's offset`() {
                fail(
                    Variance(MIN_SPLITTING_ANGLE, MIN_SPLITTING_OFFSET - fromDegrees(1)),
                    "The test's splitting angle's offset is too small!",
                )
            }

            @Test
            fun `Cannot use a too large angle's offset`() {
                fail(
                    Variance(MIN_SPLITTING_ANGLE, MAX_SPLITTING_OFFSET + fromDegrees(1)),
                    "The test's splitting angle's offset is too large!",
                )
            }
        }

        fun fail(variance: Variance<Orientation>, message: String) = fail(
            BaseSplitting(MIN_SPLITTING_PROBABILITY, variance),
            message,
        )

        fun fail(splitting: StemSplitting, message: String) {
            fail(Stem(MIN_SEGMENTS, splitting = splitting), message)
        }
    }

    fun fail(stem: Stem, message: String) {
        assertIllegalArgument(message) { stem.validate("test") }
    }
}