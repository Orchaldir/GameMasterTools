package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertInt
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
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