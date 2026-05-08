package at.orchaldir.gm.core.reducer.ecology.plant.appearance

import at.orchaldir.gm.assertFactor
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
        fun `Test the thickness factor`() {
            assertFactor(
                "test's start thickness",
                MIN_RELATIVE_TO_LENGTH,
                MAX_RELATIVE_TO_LENGTH,
                { thickness, message ->
                    fail(ConstantStemThickness(thickness), message)
                },
                { thickness ->
                    success(ConstantStemThickness(thickness))
                },
            )
        }
    }

    @Nested
    inner class LinearThicknessTest {

        @Test
        fun `Test the start thickness factor`() {
            assertFactor(
                "test's start thickness",
                MIN_RELATIVE_TO_LENGTH,
                MAX_RELATIVE_TO_LENGTH,
                { thickness, message ->
                    fail(LinearStemThickness(thickness), message)
                },
                { thickness ->
                    success(LinearStemThickness(thickness))
                },
            )
        }

        @Test
        fun `Test the end thickness factor`() {
            assertFactor(
                "test's end thickness",
                MIN_END_THICKNESS,
                MAX_END_THICKNESS,
                { thickness, message ->
                    fail(LinearStemThickness(end = thickness), message)
                },
                { thickness ->
                    success(LinearStemThickness(end = thickness))
                },
            )
        }
    }

    fun success(thickness: StemThickness) {
        val stem = Stem(MIN_SEGMENTS, thickness = thickness)

        stem.validate("test")
    }

    fun fail(thickness: StemThickness, message: String) {
        val stem = Stem(MIN_SEGMENTS, thickness = thickness)

        assertIllegalArgument(message) { stem.validate("test") }
    }
}