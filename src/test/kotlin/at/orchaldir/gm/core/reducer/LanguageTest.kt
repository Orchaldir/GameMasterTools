package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LanguageTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val state = State(Storage(Language(LANGUAGE_ID_0)))
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getLanguageStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertIllegalArgument("Requires unknown Language 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a language with children`() {
            val state = State(
                Storage(
                    listOf(
                        Language(LANGUAGE_ID_0),
                        Language(LANGUAGE_ID_1, origin = EvolvedLanguage(LANGUAGE_ID_0))
                    )
                )
            )
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertIllegalArgument("Cannot delete language 0 with children!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a language known by a character`() {
            val character = Character(CHARACTER_ID_0, languages = mapOf(LANGUAGE_ID_0 to ComprehensionLevel.Native))
            val state = State(listOf(Storage(character), Storage(Language(LANGUAGE_ID_0))))
            val action = DeleteLanguage(LANGUAGE_ID_0)

            assertIllegalArgument("Cannot delete language 0 that is known by characters!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a language used by a book`() {
            val book = Book(BOOK_ID_0, language = LANGUAGE_ID_1)
            val state = State(listOf(Storage(book), Storage(Language(LANGUAGE_ID_1))))
            val action = DeleteLanguage(LANGUAGE_ID_1)

            assertIllegalArgument("Cannot delete language 1 that is used by books!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateLanguage(Language(LANGUAGE_ID_0))

            assertIllegalArgument("Requires unknown Language 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Inventor must exist`() {
            val state = State(Storage(Language(LANGUAGE_ID_0)))
            val origin = InventedLanguage(CreatedByCharacter(CHARACTER_ID_0), DAY0)
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 0 as Inventor!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Parent language must exist`() {
            val state = State(Storage(Language(LANGUAGE_ID_0)))
            val origin = EvolvedLanguage(LANGUAGE_ID_1)
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown parent language 1!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A language cannot be its own parent`() {
            val state = State(Storage(Language(LANGUAGE_ID_0)))
            val action = UpdateLanguage(Language(LANGUAGE_ID_0, origin = EvolvedLanguage(LANGUAGE_ID_0)))

            assertIllegalArgument("A language cannot be its own parent!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Parent language exist`() {
            val state = State(Storage(listOf(Language(LANGUAGE_ID_0), Language(LANGUAGE_ID_1))))
            val language = Language(LANGUAGE_ID_0, origin = EvolvedLanguage(LANGUAGE_ID_1))
            val action = UpdateLanguage(language)

            assertEquals(language, REDUCER.invoke(state, action).first.getLanguageStorage().get(LANGUAGE_ID_0))
        }
    }

}