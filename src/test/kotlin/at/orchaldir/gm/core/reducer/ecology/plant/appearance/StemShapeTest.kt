package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertFactor
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertVariance
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import org.junit.jupiter.api.Test

class StemShapeTest {

    @Test
    fun `Test the angle`() {
        assertVariance(
            "test's curve",
            MIN_CURVE_CENTER,
            MAX_CURVE_CENTER,
            MAX_CURVE_OFFSET,
            fromDegrees(1),
            { angle, message ->
                fail(CurvedStem(angle), message)
            },
            { angle ->
                success(CurvedStem(angle))
            },
        )
    }

    fun success(shape: StemShape) {
        val stem = Stem(MIN_SEGMENTS, shape)

        stem.validate("test")
    }

    fun fail(shape: StemShape, message: String) {
        val stem = Stem(MIN_SEGMENTS, shape)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}