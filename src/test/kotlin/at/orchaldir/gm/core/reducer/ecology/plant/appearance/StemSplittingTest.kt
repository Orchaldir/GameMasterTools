package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StemSplittingTest {

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
                Variance(MIN_SPLITTING_ANGLE, -fromDegrees(1)),
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

        fun fail(variance: Variance<Orientation>, message: String) = fail(
            BaseSplitting(MIN_SPLITTING_PROBABILITY, variance),
            message,
        )
    }

    @Nested
    inner class SegmentTest {

        @Test
        fun `Cannot use a too small thickness factor`() {
            fail(
                SegmentSplitting(MIN_SPLITTING_PROBABILITY - ONE_TENTH_PERCENT),
                "The test's splitting probability factor is too small!",
            )
        }

        @Test
        fun `Cannot use a too large thickness factor`() {
            fail(
                SegmentSplitting(MAX_SPLITTING_PROBABILITY + ONE_TENTH_PERCENT),
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
                Variance(MIN_SPLITTING_ANGLE, -fromDegrees(1)),
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

        fun fail(variance: Variance<Orientation>, message: String) = fail(
            SegmentSplitting(MIN_SPLITTING_PROBABILITY, variance),
            message,
        )
    }

    fun fail(splitting: StemSplitting, message: String) {
        val stem = Stem(MIN_SEGMENTS, splitting = splitting)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}