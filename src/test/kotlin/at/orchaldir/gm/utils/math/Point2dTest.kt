package at.orchaldir.gm.utils.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Point2dTest {

    @Test
    fun `Calculate the length`() {
        assertEquals(5.0f, Point2d(-3.0f, 4.0f).length())
    }
}