package at.orchaldir.gm.utils.math

import at.orchaldir.gm.assertIllegalArgument
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LineSplitterTest {

    private val splitter = LineSplitter.fromStartAndEnd(
        Point2d(10.0f, 20f),
        Point2d(50.0f, 20f),
        ConstantWeight(2),
    )

    @Nested
    inner class GetCenterTest {

        @Test
        fun `Test valid indices`() {
            assertEquals(Point2d(20.0f, 20f), splitter.getCenter(0))
            assertEquals(Point2d(40.0f, 20f), splitter.getCenter(1))
        }

        @Test
        fun `Negative index`() {
            assertIllegalArgument("Index -1 is invalid!") { splitter.getCenter(-1) }
        }

        @Test
        fun `Index too high`() {
            assertIllegalArgument("Index 2 is invalid!") { splitter.getCenter(2) }
        }

    }

}