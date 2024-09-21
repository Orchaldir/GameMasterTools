package at.orchaldir.gm

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


inline fun <reified T : Throwable> assertFailMessage(message: String, block: () -> Unit) {
    val exception = assertFailsWith<IllegalArgumentException> { block() }
    assertEquals(message, exception.message)
}