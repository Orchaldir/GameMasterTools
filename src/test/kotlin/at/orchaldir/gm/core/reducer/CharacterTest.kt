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
private val ID1 = CharacterId(1)
private val ID2 = CharacterId(2)
private val CULTURE0 = CultureId(0)
private val LANGUAGE0 = LanguageId(0)
private val LANGUAGES = mapOf(LANGUAGE0 to ComprehensionLevel.Native)
private val PERSONALITY0 = PersonalityTraitId(0)
private val RACE0 = RaceId(0)
private val RACE1 = RaceId(1)

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
        fun `Do not overwrite languages`() {
            val state = State(
                characters = Storage(listOf(Character(ID0, languages = LANGUAGES))),
                languages = Storage(listOf(Language(LANGUAGE0))),
                personalityTraits = Storage(listOf(PersonalityTrait(PERSONALITY0))),
                races = Storage(listOf(Race(RACE0), Race(RACE1)))
            )
            val action = UpdateCharacter(Character(ID0, "Test", RACE1, Gender.Male, personality = setOf(PERSONALITY0)))

            val result = UPDATE_CHARACTER.invoke(state, action).first

            assertEquals(
                Character(
                    ID0,
                    "Test",
                    RACE1,
                    Gender.Male,
                    UndefinedCharacterOrigin,
                    null,
                    setOf(PERSONALITY0),
                    LANGUAGES
                ),
                result.characters.getOrThrow(ID0)
            )
        }

        @Nested
        inner class BornTest {
            private val UNKNOWN = CharacterId(3)

            private val state = State(
                characters = Storage(
                    listOf(
                        Character(ID0),
                        Character(ID1, gender = Gender.Male),
                        Character(ID2, gender = Gender.Female)
                    )
                ),
                races = Storage(listOf(Race(RACE0)))
            )

            @Test
            fun `Valid parents`() {
                val character = Character(ID0, origin = Born(ID2, ID1))
                val action = UpdateCharacter(character)

                val result = UPDATE_CHARACTER.invoke(state, action).first

                assertEquals(
                    character,
                    result.characters.getOrThrow(ID0)
                )
            }

            @Test
            fun `Unknown mother`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(UNKNOWN, ID1)))

                assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
            }

            @Test
            fun `Mother is not female`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(ID1, ID1)))

                assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
            }

            @Test
            fun `Unknown father`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(ID2, UNKNOWN)))

                assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
            }

            @Test
            fun `Father is not male`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(ID2, ID2)))

                assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
            }

        }

        @Test
        fun `Cannot update unknown character`() {
            val state = State(races = Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(Character(ID0))

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown culture`() {
            val state = State(characters = Storage(listOf(Character(ID0))), races = Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(Character(ID0, culture = CULTURE0))

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown personality trait`() {
            val state = State(characters = Storage(listOf(Character(ID0))), races = Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(Character(ID0, personality = setOf(PERSONALITY0)))

            assertFailsWith<IllegalArgumentException> { UPDATE_CHARACTER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown race`() {
            val state = State(characters = Storage(listOf(Character(ID0))))
            val action = UpdateCharacter(Character(ID0, race = RACE0))

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