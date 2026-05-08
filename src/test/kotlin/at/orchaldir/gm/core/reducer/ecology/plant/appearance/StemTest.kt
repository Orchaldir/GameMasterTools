package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
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

    @Test
    fun `Test the number of segments`() {
        assertInt(
            "test's number of segments",
            MIN_SEGMENTS,
            MAX_SEGMENTS,
            { segments, message ->
                fail(Stem(segments), message)
            },
            { segments ->
                success(Stem(segments))
            },
        )
    }

    fun success(stem: Stem) {
        stem.validate("test")
    }

    fun fail(stem: Stem, message: String) {
        assertIllegalArgument(message) { stem.validate("test") }
    }
}