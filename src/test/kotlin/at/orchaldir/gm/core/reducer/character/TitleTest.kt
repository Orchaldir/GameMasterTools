package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteTitle
import at.orchaldir.gm.core.action.UpdateTitle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TitleTest {
    private val title0 = Title(TITLE_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(title0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteTitle(TITLE_ID_0)

        @Test
        fun `Can delete an existing title`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getTitleStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Title 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a character`() {
            val state = state.updateStorage(Storage(Character(CHARACTER_ID_0, title = TITLE_ID_0)))

            assertIllegalArgument("Cannot delete Title 0, because it is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTitle(Title(UNKNOWN_TITLE_ID))

            assertIllegalArgument("Requires unknown Title 99!") { REDUCER.invoke(state, action) }
        }


        @Test
        fun `Update a title`() {
            val title = Title(TITLE_ID_0, NAME)
            val action = UpdateTitle(title)

            assertEquals(title, REDUCER.invoke(state, action).first.getTitleStorage().get(TITLE_ID_0))
        }
    }

}