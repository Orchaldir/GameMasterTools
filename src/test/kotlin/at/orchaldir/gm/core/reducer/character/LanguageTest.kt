package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.AddLanguage
import at.orchaldir.gm.core.action.RemoveLanguages
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val ID0 = CharacterId(0)
private val LANGUAGE0 = LanguageId(0)
private val LANGUAGES = mapOf(LANGUAGE0 to ComprehensionLevel.Native)

class LanguageTest {

    @Nested
    inner class AddLanguageTest {

        private val action = AddLanguage(ID0, LANGUAGE0, ComprehensionLevel.Native)

        @Test
        fun `Add a language`() {
            val state = State(
                characters = Storage(listOf(Character(ID0))),
                languages = Storage(listOf(Language(LANGUAGE0)))
            )

            val result = ADD_LANGUAGE.invoke(state, action).first

            assertEquals(LANGUAGES, result.characters.getOrThrow(ID0).languages)
        }

        @Test
        fun `Cannot add unknown language`() {
            val state = State(characters = Storage(listOf(Character(ID0))))

            assertFailsWith<IllegalArgumentException> { ADD_LANGUAGE.invoke(state, action) }
        }
    }

    @Nested
    inner class RemoveLanguageTest {

        private val action = RemoveLanguages(ID0, setOf(LANGUAGE0))

        @Test
        fun `Remove a language`() {
            val state = State(
                characters = Storage(listOf(Character(ID0, languages = LANGUAGES))),
                languages = Storage(listOf(Language(LANGUAGE0)))
            )

            val result = REMOVE_LANGUAGES.invoke(state, action).first

            assertTrue(result.characters.getOrThrow(ID0).languages.isEmpty())
        }

        @Test
        fun `Cannot remove unknown language`() {
            val state = State(characters = Storage(listOf(Character(ID0))))

            assertFailsWith<IllegalArgumentException> { REMOVE_LANGUAGES.invoke(state, action) }
        }

    }

}