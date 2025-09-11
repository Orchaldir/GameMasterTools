package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateTitle
import at.orchaldir.gm.core.model.State
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