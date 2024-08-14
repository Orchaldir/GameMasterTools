package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestStateTest {
    @Test
    fun `0 days between the same day`() {
        val state = TestState()
        val characters = state.getCharacters()
    }
}