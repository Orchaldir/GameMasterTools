package at.orchaldir.gm.core.reducer.font

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.FONT_ID_0
import at.orchaldir.gm.FUTURE_DAY_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteFont
import at.orchaldir.gm.core.action.UpdateFont
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val font0 = Font(FONT_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(font0),
    )
)

class FontTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteFont(FONT_ID_0)

        @Test
        fun `Can delete an existing font`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getFontStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Font 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateFont(Font(FONT_ID_0))
            val state = STATE.removeStorage(FONT_ID_0)

            assertIllegalArgument("Requires unknown Font 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateFont(Font(FONT_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Font) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a font`() {
            val font = Font(FONT_ID_0, "Test")
            val action = UpdateFont(font)

            assertEquals(font, REDUCER.invoke(STATE, action).first.getFontStorage().get(FONT_ID_0))
        }
    }

}