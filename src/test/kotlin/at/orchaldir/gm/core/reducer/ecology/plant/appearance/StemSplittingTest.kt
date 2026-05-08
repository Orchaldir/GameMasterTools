package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertFactor
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertVariance
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.Factor
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
        fun `Test the thickness factor`() {
            assertFactor(
                "test's splitting probability",
                MIN_SPLITTING_PROBABILITY,
                MAX_SPLITTING_PROBABILITY,
                { probability, message ->
                    fail(BaseSplitting(probability), message)
                },
                { probability ->
                    success(BaseSplitting(probability))
                },
            )
        }

        @Test
        fun `Test the angle`() {
            assertVariance(
                "test's splitting angle",
                MIN_SPLITTING_ANGLE,
                MAX_SPLITTING_ANGLE,
                MAX_SPLITTING_OFFSET,
                fromDegrees(1),
                { angle, message ->
                    fail(BaseSplitting(MIN_SPLITTING_PROBABILITY, angle), message)
                },
                { angle ->
                    success(BaseSplitting(MIN_SPLITTING_PROBABILITY, angle))
                },
            )
        }
    }

    @Nested
    inner class SegmentTest {

        @Test
        fun `Test the thickness factor`() {
            assertFactor(
                "test's splitting probability",
                MIN_SPLITTING_PROBABILITY,
                MAX_SPLITTING_PROBABILITY,
                { probability, message ->
                    fail(SegmentSplitting(probability), message)
                },
                { probability ->
                    success(SegmentSplitting(probability))
                },
            )
        }

        @Test
        fun `Test the angle`() {
            assertVariance(
                "test's splitting angle",
                MIN_SPLITTING_ANGLE,
                MAX_SPLITTING_ANGLE,
                MAX_SPLITTING_OFFSET,
                fromDegrees(1),
                { angle, message ->
                    fail(SegmentSplitting(MIN_SPLITTING_PROBABILITY, angle), message)
                },
                { angle ->
                    success(SegmentSplitting(MIN_SPLITTING_PROBABILITY, angle))
                },
            )
        }
    }

    fun success(splitting: StemSplitting) {
        val stem = Stem(MIN_SEGMENTS, splitting = splitting)

        stem.validate("test")
    }

    fun fail(splitting: StemSplitting, message: String) {
        val stem = Stem(MIN_SEGMENTS, splitting = splitting)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}