package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = LanguageId(0)
private val ID1 = LanguageId(1)
private val CHARACTER0 = CharacterId(0)

class LanguageTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val state = State(Storage(listOf(Language(ID0))))
            val action = DeleteLanguage(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getLanguageStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteLanguage(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a language with children`() {
            val state = State(Storage(listOf(Language(ID0), Language(ID1, origin = EvolvedLanguage(ID0)))))
            val action = DeleteLanguage(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a language known by a character`() {
            val character = Character(CHARACTER0, languages = mapOf(ID0 to ComprehensionLevel.Native))
            val state = State(listOf(Storage(listOf(character)), Storage(listOf(Language(ID0)))))
            val action = DeleteLanguage(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateLanguage(Language(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Inventor must exist`() {
            val state = State(Storage(listOf(Language(ID0))))
            val origin = InventedLanguage(CHARACTER0)
            val action = UpdateLanguage(Language(ID0, origin = origin))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Inventor exists`() {
            val state = State(
                listOf(
                    Storage(listOf(Character(CHARACTER0))),
                    Storage(listOf(Language(ID0)))
                )
            )
            val origin = InventedLanguage(CHARACTER0)
            val language = Language(ID0, origin = origin)
            val action = UpdateLanguage(language)

            assertEquals(language, REDUCER.invoke(state, action).first.getLanguageStorage().get(ID0))
        }

        @Test
        fun `Parent language must exist`() {
            val state = State(Storage(listOf(Language(ID0))))
            val origin = EvolvedLanguage(ID1)
            val action = UpdateLanguage(Language(ID0, origin = origin))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A language cannot be its own parent`() {
            val state = State(Storage(listOf(Language(ID0))))
            val action = UpdateLanguage(Language(ID0, origin = EvolvedLanguage(ID0)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Parent language exist`() {
            val state = State(Storage(listOf(Language(ID0), Language(ID1))))
            val language = Language(ID0, origin = EvolvedLanguage(ID1))
            val action = UpdateLanguage(language)

            assertEquals(language, REDUCER.invoke(state, action).first.getLanguageStorage().get(ID0))
        }
    }

}