package at.orchaldir.gm.core

import at.orchaldir.gm.core.model.ELEMENTS
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.combine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DataTest {

    @Test
    fun `Load CoC`() {
        val newState = State.load("data/CoC")

        assertEquals(ELEMENTS.size, newState.storageMap.size)
    }

    @Test
    fun `Load Eberron`() {
        val newState = State.load("data/Eberron")

        assertEquals(ELEMENTS.size, newState.storageMap.size)
    }
}