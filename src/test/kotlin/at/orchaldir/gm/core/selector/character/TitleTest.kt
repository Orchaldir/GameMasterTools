package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.TITLE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TitleTest {

    @Nested
    inner class CanDeleteTest {
        private val title = Title(TITLE_ID_0)
        private val state = State(
            listOf(
                Storage(title),
            )
        )

        @Test
        fun `Cannot delete a title used by a character`() {
            val character = Character(CHARACTER_ID_0, title = TITLE_ID_0)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(TITLE_ID_0).addId(blockingId), state.canDeleteTitle(TITLE_ID_0))
        }
    }

}