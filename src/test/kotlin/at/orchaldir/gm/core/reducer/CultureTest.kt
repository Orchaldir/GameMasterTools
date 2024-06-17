package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CultureId(0)
private val ID1 = CultureId(1)
private val CULTURE = Culture(ID0, "Test")
private val STATE = State(cultures = Storage(listOf(Culture(ID0))))

class CultureTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val action = DeleteCulture(ID0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.cultures.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCulture(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a character`() {
            val action = DeleteCulture(ID0)
            val state = STATE.copy(
                characters = Storage(
                    listOf(
                        Character(CharacterId(0), culture = ID0)
                    )
                )
            )

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

}