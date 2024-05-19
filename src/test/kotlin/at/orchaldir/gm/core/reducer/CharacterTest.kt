package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val ID0 = CharacterId(0)
private val CULTURE0 = CultureId(0)
private val LANGUAGE0 = LanguageId(0)
private val LANGUAGES = mapOf(LANGUAGE0 to ComprehensionLevel.Native)
private val PERSONALITY0 = PersonalityTraitId(0)
private val RACE0 = RaceId(0)

class CharacterTest {

    @Nested
    inner class DeleteTest {

        private val action = DeleteCharacter(ID0)

        @Test
        fun `Can delete an existing language`() {
            val state = CREATE_CHARACTER.invoke(State(), CreateCharacter).first

            assertTrue(DELETE_CHARACTER.invoke(state, action).first.languages.elements.isEmpty())
        }

        @Test
        fun `Cannot delete an inventor`() {
            val origin = InventedLanguage(ID0)
            val state = State(
                characters = Storage(listOf(Character(ID0))),
                languages = Storage(listOf(Language(LANGUAGE0, origin = origin)))
            )

            assertFailsWith<IllegalArgumentException> { DELETE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { DELETE_CHARACTER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown character`() {
            val state = State(races = Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(ID0, "Test", RACE0, Gender.Male, null, setOf())

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown culture`() {
            val state = State(characters = Storage(listOf(Character(ID0))), races = Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(ID0, "Test", RACE0, Gender.Male, CULTURE0, setOf())

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown personality trait`() {
            val state = State(characters = Storage(listOf(Character(ID0))), races = Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(ID0, "Test", RACE0, Gender.Male, null, setOf(PERSONALITY0))

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown race`() {
            val state = State(characters = Storage(listOf(Character(ID0))))
            val action = UpdateCharacter(ID0, "Test", RACE0, Gender.Male, null, setOf())

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }
    }

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
            val state = CREATE_CHARACTER.invoke(State(), CreateCharacter).first

            assertFailsWith<IllegalArgumentException> { ADD_LANGUAGE.invoke(state, action) }
        }
    }

    @Nested
    inner class RemovePersonalityTest {

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
            val state = CREATE_CHARACTER.invoke(State(), CreateCharacter).first

            assertFailsWith<IllegalArgumentException> { REMOVE_LANGUAGES.invoke(state, action) }
        }

    }

}