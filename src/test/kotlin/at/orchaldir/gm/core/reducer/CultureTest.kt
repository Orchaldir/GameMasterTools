package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CultureId(0)
private val NL_ID0 = NameListId(0)
private val C_ID0 = CharacterId(0)
private val C_ID1 = CharacterId(1)
private val NAMES0 = NameList(NL_ID0)
private val STATE = State(cultures = Storage(listOf(Culture(ID0))))
private val STATE_WITH_NAMES = STATE.copy(nameLists = Storage(listOf(NAMES0)))

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
            val action = UpdateCulture(Culture(ID0, namingConvention = MononymConvention(NL_ID0)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Can update culture with known name list`() {
            val culture = Culture(ID0, namingConvention = MononymConvention(NL_ID0))
            val action = UpdateCulture(culture)

            assertEquals(Storage(listOf(culture)), REDUCER.invoke(STATE_WITH_NAMES, action).first.cultures)
        }

        @Nested
        inner class ChangingToNoNameConventionTest {

            @Test
            fun `From family to no convention`() {
                changeToNo(FamilyConvention(), FamilyName("A", null, "B"))
            }

            @Test
            fun `From genonym to no convention`() {
                changeToNo(GenonymConvention(), Genonym("A"))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToNo(PatronymConvention(), Genonym("A"))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToNo(MatronymConvention(), Genonym("A"))
            }

            private fun changeToNo(old: NamingConvention, name: CharacterName) =
                changeConvention(old, NoNamingConvention, name)
        }

        private fun changeConvention(old: NamingConvention, new: NamingConvention, name: CharacterName) {
            val action = UpdateCulture(Culture(ID0, namingConvention = new))
            val character0 = Character(C_ID0, name, culture = ID0)
            val character1 = Character(C_ID1, Mononym("Z"))
            val result = Character(C_ID0, Mononym("A"), culture = ID0)
            val state = State(
                characters = Storage(listOf(character0, character1)),
                cultures = Storage(listOf(Culture(ID0, namingConvention = old))),
                nameLists = Storage(listOf(NAMES0))
            )

            val newState = REDUCER.invoke(state, action)

            assertEquals(result, newState.first.characters.getOrThrow(C_ID0))
            assertEquals(character1, newState.first.characters.getOrThrow(C_ID1))
        }
    }

}