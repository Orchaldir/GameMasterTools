package at.orchaldir.gm

import at.orchaldir.gm.utils.math.Point2d
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


inline fun <reified T : Throwable> assertFailMessage(message: String, block: () -> Unit) {
    val exception = assertFailsWith<T> { block() }
    assertEquals(message, exception.message)
}

fun assertIllegalArgument(message: String, block: () -> Unit) =
    assertFailMessage<IllegalArgumentException>(message, block)

fun assertIllegalState(message: String, block: () -> Unit) =
    assertFailMessage<IllegalStateException>(message, block)

fun assertPoints(expected: List<Point2d>, actual: List<Point2d>, threshold: Float = 0.001f) {
    assertEquals(expected.size, actual.size)

    expected.zip(actual).withIndex().forEach { (index, pair) ->
        assertEquals(expected.size, actual.size)
        val distance = pair.first.calculateDistance(pair.second).toMeters()
        assertTrue(distance < threshold) {
            "The points with index $index are too far apart! d=$distance > $threshold"
        }
    }
}