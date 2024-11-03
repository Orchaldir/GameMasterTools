package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalSkin
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Distance
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)

class AppearanceTest {

    private val appearance = HeadOnly(Head(skin = NormalSkin()), Distance(500))
    private val action = UpdateAppearance(ID0, appearance)

    @Test
    fun `Update appearance`() {
        val state = State(Storage(listOf(Character(ID0))))

        val result = REDUCER.invoke(state, action).first

        assertEquals(appearance, result.getCharacterStorage().getOrThrow(ID0).appearance)
    }

    @Test
    fun `Cannot update unknown character`() {
        val state = State()

        assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
    }

}