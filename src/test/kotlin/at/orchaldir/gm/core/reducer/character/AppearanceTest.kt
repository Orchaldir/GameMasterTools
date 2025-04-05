package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalSkin
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Distribution
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AppearanceTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0)),
            Storage(Race(RACE_ID_0, height = Distribution.fromMeters(1.0f, 0.1f))),
        )
    )
    private val appearance = HeadOnly(Head(), fromMillimeters(1000))
    private val action = UpdateAppearance(CHARACTER_ID_0, appearance)

    @Test
    fun `Update appearance`() {
        val result = REDUCER.invoke(state, action).first

        assertEquals(appearance, result.getCharacterStorage().getOrThrow(CHARACTER_ID_0).appearance)
    }

    @Test
    fun `Cannot update unknown character`() {
        val state = State()

        assertIllegalArgument("Requires unknown Character 0!") { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Character's height is invalid!`() {
        val appearance = HeadOnly(Head(), fromMillimeters(1101))
        val action = UpdateAppearance(CHARACTER_ID_0, appearance)

        assertIllegalArgument("Character's height is invalid!") { REDUCER.invoke(state, action) }
    }

}