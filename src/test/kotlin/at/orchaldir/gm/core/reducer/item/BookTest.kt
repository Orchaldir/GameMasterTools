package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteBook
import at.orchaldir.gm.core.action.UpdateBook
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.OriginalBook
import at.orchaldir.gm.core.model.item.book.TranslatedBook
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private val STATE = State(
    listOf(
        Storage(listOf(Book(BOOK_ID_0), Book(BOOK_ID_1))),
        Storage(Character(CHARACTER_ID_0)),
        Storage(Language(LANGUAGE_ID_0)),
    )
)

class BookTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteBook(BOOK_ID_0)

        @Test
        fun `Can delete an existing book`() {
            assertFalse(REDUCER.invoke(STATE, action).first.getBookStorage().contains(BOOK_ID_0))
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Book 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBook(Book(BOOK_ID_0))

            assertIllegalArgument("Requires unknown Book 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Author must exist`() {
            val origin = OriginalBook(CreatedByCharacter(CHARACTER_ID_1))
            val action = UpdateBook(Book(BOOK_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 1 as Author!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Successfully update an original book`() {
            val origin = OriginalBook(CreatedByCharacter(CHARACTER_ID_0))
            val book = Book(BOOK_ID_0, origin = origin)
            val action = UpdateBook(book)

            assertEquals(book, REDUCER.invoke(STATE, action).first.getBookStorage().get(BOOK_ID_0))
        }

        @Test
        fun `Translator must exist`() {
            val origin = TranslatedBook(BOOK_ID_1, CreatedByCharacter(CHARACTER_ID_1))
            val action = UpdateBook(Book(BOOK_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 1 as Translator!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Translated book must exist`() {
            val origin = TranslatedBook(BOOK_ID_2, CreatedByCharacter(CHARACTER_ID_0))
            val action = UpdateBook(Book(BOOK_ID_0, origin = origin))

            assertIllegalArgument("Requires unknown Book 2!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Successfully update a translated book`() {
            val origin = TranslatedBook(BOOK_ID_1, CreatedByCharacter(CHARACTER_ID_0))
            val book = Book(BOOK_ID_0, origin = origin)
            val action = UpdateBook(book)

            assertEquals(book, REDUCER.invoke(STATE, action).first.getBookStorage().get(BOOK_ID_0))
        }
    }

}