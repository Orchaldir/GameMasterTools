package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.MononymConvention
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CultureId(0)
private val NL0 = NameListId(0)
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

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCulture(Culture(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot update culture with unknown name list`() {
            val action = UpdateCulture(Culture(ID0, namingConvention = MononymConvention(NL0)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cant update culture with known name list`() {
            val culture = Culture(ID0, namingConvention = MononymConvention(NL0))
            val action = UpdateCulture(culture)
            val state = STATE.copy(nameLists = Storage(listOf(NameList(NL0))))

            assertEquals(Storage(listOf(culture)), REDUCER.invoke(state, action).first.cultures)
        }
    }

}