package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteBook
import at.orchaldir.gm.core.action.UpdateBook
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.OriginalBook
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val STATE = State(
    listOf(
        Storage(Book(BOOK_ID_0)),
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
            assertEquals(0, REDUCER.invoke(STATE, action).first.getBookStorage().getSize())
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
        fun `Inventor must exist`() {
            val origin = OriginalBook(CreatedByCharacter(CHARACTER_ID_1))
            val action = UpdateBook(Book(BOOK_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 1 as Author!") { REDUCER.invoke(STATE, action) }
        }
    }

}