package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StemThicknessTest {

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
        val stem = Stem(MIN_SEGMENTS, thickness = thickness)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}