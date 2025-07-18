package at.orchaldir.gm.utils.math

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.utils.math.Point2d.Companion.fromMeters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SegmentSplitterTest {

    private val start = fromMeters(10.0f, 20f)
    private val end = fromMeters(50.0f, 20f)
    private val center0 = fromMeters(20.0f, 20f)
    private val center1 = fromMeters(40.0f, 20f)

    private val splitter = SegmentSplitter.fromStartAndEnd(
        start,
        end,
        ConstantWeight(2),
    )


    @Nested
    inner class GetCenterTest {

        @Test
        fun `Test valid indices`() {
            assertEquals(center0, splitter.getCenter(0))
            assertEquals(center1, splitter.getCenter(1))
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

    @Test
    fun `Get all centers`() {
        assertEquals(listOf(center0, center1), splitter.getCenters())
    }

    @Test
    fun `Get all corners`() {
        assertEquals(listOf(start, fromMeters(30.0f, 20f), end), splitter.getCorners())
    }

}