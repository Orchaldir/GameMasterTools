package at.orchaldir.gm.core

import at.orchaldir.gm.core.model.State
import org.junit.jupiter.api.Test

class DataTest {

    @Test
    fun `Load Dolmenwood`() {
        test("data/Dolmenwood")
    }

    @Test
    fun `Load GURPS`() {
        test("data/GURPS")
    }

    private fun test(path: String) {
        val newState = State.load(path)

        newState.validate()
    }
}