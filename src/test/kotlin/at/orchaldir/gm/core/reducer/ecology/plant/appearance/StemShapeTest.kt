package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import org.junit.jupiter.api.Test

class StemShapeTest {

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
        val angle = Variance(ZERO_ORIENTATION, -fromDegrees(1))

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
        val stem = Stem(MIN_SEGMENTS, shape)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}