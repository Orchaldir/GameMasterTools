package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.RaceId
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val ID0 = CharacterId(0)
private val ID1 = CharacterId(1)
private val RACE0 = RaceId(0)
private val CULTURE0 = CultureId(0)

class CharacterTest {

    @Test
    fun `Can delete an existing language`() {
        val state = CREATE_CHARACTER.invoke(State(), CreateCharacter).first
        val action = DeleteCharacter(ID0)

        assertTrue(DELETE_CHARACTER.invoke(state, action).first.languages.elements.isEmpty())
    }

    @Test
    fun `Cannot delete unknown id`() {
        val action = DeleteCharacter(ID0)

        assertFailsWith<IllegalArgumentException> { DELETE_CHARACTER.invoke(State(), action) }
    }

    @Test
    fun `Cannot update unknown id`() {
        val action = UpdateCharacter(ID0, "Test", RACE0, Gender.Male, CULTURE0)

        assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(State(), action) }
    }

}