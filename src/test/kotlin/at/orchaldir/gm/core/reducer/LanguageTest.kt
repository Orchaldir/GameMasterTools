package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val ID0 = LanguageId(0)
private val ID1 = LanguageId(1)

class LanguageTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val state = CREATE_LANGUAGE.invoke(State(), CreateLanguage).first
            val action = DeleteLanguage(ID0)

            assertTrue(DELETE_LANGUAGE.invoke(state, action).first.languages.elements.isEmpty())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteLanguage(ID0)

            assertFailsWith<IllegalArgumentException> { DELETE_LANGUAGE.invoke(State(), action) }
        }

        @Test
        fun `Can delete a language with children`() {
            val state = State(languages = Storage(listOf(Language(ID0), Language(ID1, origin = EvolvedLanguage(ID0)))))
            val action = DeleteLanguage(ID0)

            assertFailsWith<IllegalArgumentException> { DELETE_LANGUAGE.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateLanguage(Language(ID0))

            assertFailsWith<IllegalArgumentException> { UPDATE_LANGUAGE.invoke(State(), action) }
        }

        @Test
        fun `Inventor must exist`() {
            val state = CREATE_LANGUAGE.invoke(State(), CreateLanguage).first
            val origin = InventedLanguage(CharacterId(0))
            val action = UpdateLanguage(Language(ID0, origin = origin))

            assertFailsWith<IllegalArgumentException> { UPDATE_LANGUAGE.invoke(state, action) }
        }

        @Test
        fun `Inventor exists`() {
            val state0 = CREATE_CHARACTER.invoke(State(), CreateCharacter).first
            val state1 = CREATE_LANGUAGE.invoke(state0, CreateLanguage).first
            val origin = InventedLanguage(CharacterId(0))
            val language = Language(ID0, origin = origin)
            val action = UpdateLanguage(language)

            assertEquals(language, UPDATE_LANGUAGE.invoke(state1, action).first.languages.get(ID0))
        }

        @Test
        fun `Parent language must exist`() {
            val state = CREATE_LANGUAGE.invoke(State(), CreateLanguage).first
            val origin = EvolvedLanguage(ID1)
            val action = UpdateLanguage(Language(ID0, origin = origin))

            assertFailsWith<IllegalArgumentException> { UPDATE_LANGUAGE.invoke(state, action) }
        }

        @Test
        fun `A language cannot be its own parent`() {
            val state = CREATE_LANGUAGE.invoke(State(), CreateLanguage).first
            val action = UpdateLanguage(Language(ID0, origin = EvolvedLanguage(ID0)))

            assertFailsWith<IllegalArgumentException> { UPDATE_LANGUAGE.invoke(state, action) }
        }

        @Test
        fun `Parent language exist`() {
            val state0 = CREATE_LANGUAGE.invoke(State(), CreateLanguage).first
            val state1 = CREATE_LANGUAGE.invoke(state0, CreateLanguage).first
            val language = Language(ID0, origin = EvolvedLanguage(ID1))
            val action = UpdateLanguage(language)

            assertEquals(language, UPDATE_LANGUAGE.invoke(state1, action).first.languages.get(ID0))
        }
    }

}